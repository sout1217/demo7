package com.example.demo7.security.listener;

import com.example.demo7.domain.entity.Account;
import com.example.demo7.domain.entity.Resources;
import com.example.demo7.domain.entity.Role;
import com.example.demo7.repository.ResourcesRepository;
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
        Set<Role> roles = new HashSet<>();

        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        createResourcesIfNotFound("/admin/**", "", roles, "url");
        Account account = createUserIfNotFound("admin", "root", "admin@gmail.com", "10", roles);

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
