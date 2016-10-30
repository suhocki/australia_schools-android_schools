package au.com.websitemasters.schools.lcps.utils;

public interface ServerObserverRetrofit<Object, String> {

    void successExecuteObject(Object obj);

    void failedExecute(String err);
}