package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.UserCredential;

import java.io.Serializable;
import java.util.Set;

/**
 * @author cjbi
 */
public record UserinfoDTO(String token, Long userId, String username, String avatar,
                          Credential credential, Set<String> permissions) implements Serializable {

    public record Credential(String identifier, UserCredential.IdentityType type) {
    }

}
