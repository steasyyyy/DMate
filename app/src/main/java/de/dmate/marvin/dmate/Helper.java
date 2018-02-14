package de.dmate.marvin.dmate;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Helper {

    //HELPER CLASS to access Application Object from everywhere
    //EXAMPLE: Helper.getInstance().getApplication().getNextID();

    private static final Helper ourInstance = new Helper();
    public DMateApplication app;

    public static Helper getInstance() {
        return ourInstance;
    }

    private Helper() {
    }

    public void setApplication(DMateApplication application) {
        app = application;
    }

    public DMateApplication getApplication() {
        return app;
    }
}
