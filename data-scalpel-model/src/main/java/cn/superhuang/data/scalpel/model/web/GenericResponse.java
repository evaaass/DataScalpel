package cn.superhuang.data.scalpel.model.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Data
@Schema(description = "通用返回结果")
public class GenericResponse<T> {
    @Schema(description = "信息编号")
    private String code;
    @Schema(description = "错误信息")
    private String message;
    @Schema(description = "详细错误信息")
    private String detail;
    @Schema(description = "返回结果对象")
    private T data;

    public static <T> GenericResponse<T> ok(T data) {
        GenericResponse res = new GenericResponse();
        res.setCode("200");
        res.setData(data);
        return res;
    }

    public static <T> GenericResponse<T> ok() {
        return ok(null);
    }

    public static <T> GenericResponse<T> error(String msg) {
        GenericResponse res = new GenericResponse();
        res.setMessage(msg);
        return res;
    }

    public static <T> GenericResponse<T> error(String msg, String detail) {
        GenericResponse res = new GenericResponse();
        res.setMessage(msg);
        res.setDetail(detail);
        return res;
    }

    public static <T> GenericResponse<T> error(String code, String msg, String detail) {
        GenericResponse res = new GenericResponse();
        res.setCode(code);
        res.setMessage(msg);
        res.setDetail(detail);
        return res;
    }

    public static <X> GenericResponse<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return (GenericResponse) maybeResponse.map((response) -> {
            return GenericResponse.ok(response);
        }).orElseThrow(() -> {
            return new RuntimeException("Not Found");
        });
    }
}
