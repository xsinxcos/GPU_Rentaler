package com.gpu.rentaler.common;


/**
 * @author cjbi
 * @date 2022/7/22
 */
public interface EventStore {

    void append(DomainEvent aDomainEvent);

}
