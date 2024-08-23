package cn.superhuang.data.scalpel.admin.app.datafile.web.resource;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DataFileResource implements IDataFileResource {

    @Override
    @ResponseBody
    public GenericResponse<Page<DataFile>> search(GenericSearchRequestDTO searchRequest) {
        return null;
    }

    @Override
    @ResponseBody
    public GenericResponse<String> createFile(String alias, String catalogId, DataFileType type, String description, String options, MultipartFile file) {
        return null;
    }

    @Override
    @ResponseBody
    public GenericResponse<Void> updateFileAssert(String id, String alias, String catalogId, DataFileType type, String description, String options, MultipartFile file) {
        return null;
    }

    @Override
    @ResponseBody
    public GenericResponse<Void> delete(String type, String id) {
        return null;
    }
}
