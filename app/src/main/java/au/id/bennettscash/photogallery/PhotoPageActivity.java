package au.id.bennettscash.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by chris on 3/09/15.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
