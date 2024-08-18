package cn.superhuang.data.scalpel.admin.config;

import cn.superhuang.data.scalpel.model.web.GenericResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice

public class GlobalExceptionInterceptor {

//    @ResponseBody
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    public GenericResponse exceptionHandler(HttpServletRequest request, Exception e) {
//        GenericResponse executionResponse = new GenericResponse();
//        String failMsg = null;
//        if (e instanceof MethodArgumentNotValidException) {
//            // 拿到参数校验具体异常信息提示
//            failMsg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage();
//            executionResponse = ApiResult.buildHystrixError(failMsg);
//        }
//        // 将消息返回给前端
//        return executionResponse;
//    }

    /**
     * 异常处理
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public GenericResponse exceptionHandler(Exception e) {
        e.printStackTrace();
        GenericResponse res = new GenericResponse();
        res.setCode("500");
        res.setMessage(ExceptionUtils.getMessage(e));
        res.setDetail(ExceptionUtils.getStackTrace(e));
        return res;
    }


    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
//        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        problemDetail.setType(URI.create("https://superhuang/problems/400"));
//        problemDetail.setTitle("请求参数不正确");
//        problemDetail.setDetail(errors.toString());
//        return problemDetail;
//    }
//    @ResponseBody
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ProblemDetail handleValidationExceptions(ConstraintViolationException ex) {
//        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        problemDetail.setType(URI.create("https://superhuang/problems/400"));
//        problemDetail.setTitle("请求参数不正确");
//        problemDetail.setDetail(ex.getMessage());
//        return problemDetail;
//    }


//    /**
//     * 处理所有RequestParam注解数据验证异常
//     *
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler(BindException.class)
//    public ApiResult handleBindException(BindException ex) {
//        return ApiResult.buildHystrixError(ex.getMessage());
//    }
}
