package mmda.mmdamobilepayment.data.source;

import mmda.mmdamobilepayment.BasePresenter;
import mmda.mmdamobilepayment.data.model.Driver;
import mmda.mmdamobilepayment.data.source.remote.AppAPIHelper;

/**
 * Created by Raniel on 11/17/2018.
 */

public class DriverDataRepository implements DriverRepository {

    @Override
    public void attemptLogin(BasePresenter presenter, Driver driver) {
        AppAPIHelper apiHelper = new AppAPIHelper(presenter);
        apiHelper.execute("Login", driver);
    }

    @Override
    public void getDriverDetailsInCloud(BasePresenter presenter, Driver driver) {
        AppAPIHelper apiHelper = new AppAPIHelper(presenter);
        apiHelper.execute("GetDriverDetails", driver);
    }

}
