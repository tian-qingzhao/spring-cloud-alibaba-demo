package com.tqz.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 公共返回类
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/25 11:15
 */
@Data
@ApiModel(value = "统一返回结果封装", description = "接口返回统一结果")
public class ResultData<T> {

    /**
     * 结果状态 ,具体状态码参见 {@link ReturnCode}
     */
    @ApiModelProperty(value = "状态码")
    private int code;
    @ApiModelProperty(value = "响应信息")
    private String message;
    @ApiModelProperty(value = "后端返回结果")
    private T data;
    @ApiModelProperty(value = "响应时间戳")
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC100.getCode());
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> success() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        return resultData;
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC999.getCode());
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> ResultData<T> fail() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC999.getCode());
        resultData.setMessage(ReturnCode.RC999.getMessage());
        return resultData;
    }
}
