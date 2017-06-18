package test;

import android.app.Activity;

import com.github.slick.OnDestroyListener;
import com.github.slick.SlickDelegateActivity;

import java.lang.Override;
import java.lang.String;
import java.util.HashMap;

public class DaggerActivity_Slick implements OnDestroyListener {

    private static DaggerActivity_Slick hostInstance;
    private final HashMap<String, SlickDelegateActivity<ExampleView, ExamplePresenter>> delegates = new HashMap<>();

    public static <T extends Activity & ExampleView> void bind(T daggerActivity) {
        final String id = SlickDelegateActivity.getId(daggerActivity);
        if (hostInstance == null) hostInstance = new DaggerActivity_Slick();
        SlickDelegateActivity<ExampleView, ExamplePresenter> delegate = hostInstance.delegates.get(id)
        if (delegate == null) {
            final ExamplePresenter presenter = ((DaggerActivity) daggerActivity).provider.get();
            delegate = new SlickDelegateActivity<>(presenter, daggerActivity.getClass(), id);
            delegate.setListener(hostInstance);
            hostInstance.delegates.put(id, delegate);
            daggerActivity.getApplication().registerActivityLifecycleCallbacks(delegate);
        }
        ((DaggerActivity) daggerActivity).presenter = delegate.getPresenter();
    }

    @Override
    public void onDestroy(String id) {
        hostInstance.delegates.remove(id);
        if (hostInstance.delegates.size() == 0) {
            hostInstance = null;
        }
    }
}