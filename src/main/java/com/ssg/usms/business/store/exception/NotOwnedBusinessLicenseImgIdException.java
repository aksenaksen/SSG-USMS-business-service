package com.ssg.usms.business.store.exception;

import com.ssg.usms.business.error.ErrorResponseDto;
import lombok.Getter;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_MESSAGE;

@Getter
public class NotOwnedBusinessLicenseImgIdException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto =
            new ErrorResponseDto(NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_CODE, NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_MESSAGE);

}
