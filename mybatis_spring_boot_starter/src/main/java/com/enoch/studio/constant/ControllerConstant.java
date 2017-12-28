package com.enoch.studio.constant;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by Enoch on 2017/12/12.
 */
public class ControllerConstant {
    public static final String PATH_VARIABLE_NAME = "ip";
    public static final String URL_SUFFIX = "-{" + PATH_VARIABLE_NAME + "}";
}
