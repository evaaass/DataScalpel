package cn.superhuang.data.scalpel.admin.app.datafile.repository;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Repository
public interface DataFileRepository extends JpaRepository<DataFile, String>, JpaSpecificationExecutor<DataFile> {

}
