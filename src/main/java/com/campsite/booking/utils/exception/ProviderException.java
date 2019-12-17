package com.campsite.booking.utils.exception;


public class ProviderException extends RuntimeException {
    private static final long serialVersionUID = 148063203280743829L;

    private ProviderException(String detail, Throwable cause) {
        super(detail, cause);
    }

    public static ProviderExceptionBuilder builder(String detail) {
        return new ProviderException(detail, null).new ProviderExceptionBuilder();
    }

    public static ProviderExceptionBuilder builder(String detail, Throwable cause) {
        return new ProviderException(detail, cause).new ProviderExceptionBuilder();
    }

    public class ProviderExceptionBuilder {
        public ProviderException build() {
            return ProviderException.this;
        }
    }

}
