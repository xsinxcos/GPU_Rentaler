package com.gpu.rentaler.infra.service;

import com.gpu.rentaler.sys.model.UserCredential;

import java.io.Serializable;

/**
 * @author wzq
 */
public interface SessionManager {

    void store(String key, UserCredential credential, Serializable value);

    void invalidate(String key);

    Object get(String key);

    void refresh();
}
