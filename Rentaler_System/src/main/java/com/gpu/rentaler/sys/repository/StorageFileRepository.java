package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.StorageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author cjbi
 */
@Repository
public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {

    @Query("from StorageFile where key=:key")
    StorageFile getByKey(String key);

    @Modifying
    @Query("from StorageFile where key=:key")
    void deleteByKey(String key);
}
