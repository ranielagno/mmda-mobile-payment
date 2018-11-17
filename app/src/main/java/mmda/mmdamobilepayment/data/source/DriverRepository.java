package mmda.mmdamobilepayment.data.source;

import mmda.mmdamobilepayment.BasePresenter;
import mmda.mmdamobilepayment.data.model.Driver;

/**
 * Created by Raniel on 11/17/2018.
 */

public interface DriverRepository {

    public void attemptLogin(BasePresenter presenter, Driver driver);

    public void getDriverDetailsInCloud(BasePresenter presenter, Driver driver);

}
