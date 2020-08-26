package com.example.demo7.controller.admin;

import com.example.demo7.domain.dto.ResourcesDto;
import com.example.demo7.domain.entity.Resources;
import com.example.demo7.domain.entity.Role;
import com.example.demo7.repository.RoleRepository;
import com.example.demo7.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.demo7.service.ResourcesService;
import com.example.demo7.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ResourcesController {

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    @PostMapping("/admin/resources")
    public String createResources(ResourcesDto resourcesDto) {
        ModelMapper modelMapper = new ModelMapper();
        Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        Resources resources = modelMapper.map(resourcesDto, Resources.class);
        resources.setRoleSet(roles);

        resourcesService.createResources(resources);
        filterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources")
    public String getResources(Model model) {

        List<Resources> resources = resourcesService.getResources();

        model.addAttribute("resources", resources);

        return "admin/resource/list";
    }

    @GetMapping(value="/admin/resources/register")
    public String viewRoles(Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);

        ResourcesDto resources = new ResourcesDto();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        resources.setRoleSet(roleSet);
        model.addAttribute("resources", resources);

        return "admin/resource/detail";
    }

    @GetMapping(value="/admin/resources/{id}")
    public String getResources(@PathVariable String id, Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        Resources resources = resourcesService.getResources(Long.valueOf(id));


        ModelMapper modelMapper = new ModelMapper();
        ResourcesDto resourcesDto = modelMapper.map(resources, ResourcesDto.class);
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String removeResources(@PathVariable String id, Model model) {
//        Resources resources = resourcesService.getResources(Long.valueOf(id));
        resourcesService.deleteResources(Long.valueOf(id));
        filterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }
}
