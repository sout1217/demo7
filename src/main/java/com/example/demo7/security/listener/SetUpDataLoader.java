package com.example.demo7.security.listener;

import com.example.demo7.domain.entity.Account;
import com.example.demo7.domain.entity.Resources;
import com.example.demo7.domain.entity.Role;
import com.example.demo7.domain.entity.RoleHierarchy;
import com.example.demo7.repository.ResourcesRepository;
import com.example.demo7.repository.RoleHierarchyRepository;
import com.example.demo7.repository.RoleRepository;
import com.example.demo7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;


    private static AtomicInteger count = new AtomicInteger(0);

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        
        if (alreadySetup) {
            return;
        }

        setupSecurityResources();
        
        alreadySetup = true;
    }

    private void setupSecurityResources() {
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();
        Set<Role> userRoles = new HashSet<>();

        Set<Role> adminRolesResources = new HashSet<>();
        Set<Role> managerRolesResources = new HashSet<>();
        Set<Role> userRolesResources = new HashSet<>();


        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        Role userRole = createRoleIfNotFound("ROLE_USER", "유저");

        adminRoles.add(adminRole);
        adminRoles.add(managerRole);
        adminRoles.add(userRole);

        managerRoles.add(managerRole);
        managerRoles.add(userRole);

        userRoles.add(userRole);

        createUserIfNotFound("admin", "root", "admin@gmail.com", "10", adminRoles);
        createUserIfNotFound("manager", "root", "manager@gmail.com", "10", managerRoles);
        createUserIfNotFound("user", "root", "user@gmail.com", "10", userRoles);

        adminRolesResources.add(adminRole);
        managerRolesResources.add(managerRole);
        userRolesResources.add(userRole);

        createResourcesIfNotFound("/admin/**", "", adminRolesResources, "url");
        createResourcesIfNotFound("/mypage", "", userRolesResources, "url");
        createResourcesIfNotFound("/messages", "", managerRolesResources, "url");
        createResourcesIfNotFound("/config", "", adminRolesResources, "url");

        createRoleHierarchyIfNotFound(managerRole, adminRole);
        createRoleHierarchyIfNotFound(userRole, managerRole);

}

    @Transactional
    public void createRoleHierarchyIfNotFound(Role childRole, Role parentRole) {

        // 부무 조회
        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());

        // 부모가 없는 경우
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(parentRole.getRoleName())
                    .build();
        }
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(childRole.getRoleName())
                    .build();
        }
        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);
    }

    @Transactional
    public Role createRoleIfNotFound(String roleName, String roleDesc) {

        Role role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }

        return roleRepository.save(role);
    }

    @Transactional
    public Account createUserIfNotFound(String userName, String password, String email, String age, Set<Role> roleSet) {

        Account account = userRepository.findByUsername(userName);

        if (account == null) {
            account = Account.builder()
                        .username(userName)
                        .password(passwordEncoder.encode(password))
                        .email(email)
                        .age(age)
                        .userRoles(roleSet)
                        .build();
        }

        return userRepository.save(account);
    }

    @Transactional
    public Resources createResourcesIfNotFound(String resourceName, String httpMethod, Set<Role> roleSet, String resourceType) {

        Resources resource = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);

        if (resource == null) {
            resource = Resources.builder()
                    .resourceName(resourceName)
                    .roleSet(roleSet)
                    .httpMethod(httpMethod)
                    .resourceType(resourceType)
                    .orderNum(count.incrementAndGet())
                    .build();
        }

        return resourcesRepository.save(resource);
    }
}
