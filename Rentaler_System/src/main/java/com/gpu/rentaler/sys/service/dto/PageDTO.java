package com.gpu.rentaler.sys.service.dto;

import java.util.List;

/**
 * @author wzq
 */
public record PageDTO<T>(List<T> list, long total) {

}
