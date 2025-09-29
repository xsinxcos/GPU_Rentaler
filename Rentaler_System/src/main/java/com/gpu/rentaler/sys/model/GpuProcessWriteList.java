package com.gpu.rentaler.sys.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "gpu_process_write_list", schema = "gpu_rentaler_0")
public class GpuProcessWriteList extends BaseEntity{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
