package com.epam.digital.data.platform.bphistory.service.api.repository;

import com.epam.digital.data.platform.bphistory.service.api.repository.entity.UpdaptableBpmHistoryProcess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdaptableProcessRepository extends CrudRepository<UpdaptableBpmHistoryProcess, String> {
}
