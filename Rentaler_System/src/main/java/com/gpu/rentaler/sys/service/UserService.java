package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.common.*;
import com.gpu.rentaler.sys.event.UserCreated;
import com.gpu.rentaler.sys.event.UserDeleted;
import com.gpu.rentaler.sys.event.UserUpdated;
import com.gpu.rentaler.sys.exception.UserException;
import com.gpu.rentaler.sys.model.Organization;
import com.gpu.rentaler.sys.model.User;
import com.gpu.rentaler.sys.model.UserCredential;
import com.gpu.rentaler.sys.repository.UserCredentialRepository;
import com.gpu.rentaler.sys.repository.UserRepository;
import com.gpu.rentaler.sys.service.dto.OrgUserDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import jakarta.validation.constraints.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gpu.rentaler.common.CommonResultStatus.RECORD_NOT_EXIST;

/**
 * @author cjbi
 */
@Service
public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    private final UserCredentialRepository userCredentialRepository;

    public UserService(UserRepository userRepository, UserCredentialRepository userCredentialRepository) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
    }

    @Transactional
    public User createUser(String username, String avatar, User.Gender gender, User.State state, Organization organization) {
        User user = new User();
        user.setUsername(username);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setState(state);
        user.setCreatedTime(LocalDateTime.now());
        user.setOrganization(organization);
        user = userRepository.save(user);
        DomainEventPublisher.instance().publish(new UserCreated(user));
        return user;
    }

    public Set<User> findUserByIds(Set<Long> userIds) {
        return userRepository.findByIds(userIds);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(RECORD_NOT_EXIST));
    }

    public PageDTO<OrgUserDTO> findOrgUsers(Pageable pageable, String username, User.State state, Organization organization) {
        Page<User> page = userRepository.findOrgUsers(pageable, username, state, organization, organization.makeSelfAsParentIds());
        return new PageDTO<>(page.getContent().stream().map(u ->
                new OrgUserDTO(u.getId(), u.getUsername(), u.getAvatar(), u.getGender(), u.getState(), u.getOrgFullName(), u.getCreatedTime()))
            .collect(Collectors.toList()), page.getTotalElements());
    }

    public boolean existsUsers(Organization organization) {
        String orgParentIds = organization.makeSelfAsParentIds();
        return userRepository.countOrgUsers(organization, orgParentIds) > 0;
    }


    @Transactional
    public User updateUser(Long userId, String avatar, User.Gender gender, User.State state, Organization organization) {
        User user = findUserById(userId);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setState(state);
        user.setOrganization(organization);
        user = userRepository.save(user);
        DomainEventPublisher.instance().publish(new UserUpdated(user));
        return user;
    }

    @Transactional
    public User disableUser(Long userId) {
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        if (Objects.equals(userInfo.userId(), userId)) {
            throw new UserException(CommonResultStatus.PARAM_ERROR, "不能禁用自己");
        }
        User user = findUserById(userId);
        user.setState(User.State.LOCKED);
        return userRepository.save(user);
    }

    @Transactional
    public User enableUser(Long userId) {
        User user = findUserById(userId);
        user.setState(User.State.NORMAL);
        return userRepository.save(user);
    }

    public PageDTO<User> findUsers(Pageable pageable, User user) {
        Page<User> page = userRepository.findAll(Example.of(user), pageable);
        return new PageDTO<>(page.getContent(), page.getTotalElements());
    }

    @Transactional
    public void delete(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
        DomainEventPublisher.instance().publish(new UserDeleted(user));
    }

    public User register(String username,String password) {
        try {
            User user = new User();
            user.setGender(User.Gender.MALE);
            user.setUsername(username);
            user.setState(User.State.NORMAL);

            UserCredential credential = new UserCredential();
            credential.setIdentifier(username);
            credential.setUser(user);
            credential.setIdentityType(UserCredential.IdentityType.PASSWORD);
            credential.setCredential(SecurityUtil.md5(user.getUsername() ,password));

            user.setCredentials(Set.of(credential));

            userRepository.save(user);

            return user;
        } catch (NoSuchAlgorithmException e) {
            log.error("e: {}" ,e.getMessage());
        }
        return null;
    }
}
