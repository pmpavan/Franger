package com.frangerapp.franger.viewmodel.invite;

import android.content.Context;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.viewmodel.BaseViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 07/02/18.
 */

public class InviteUserViewModel extends BaseViewModel {


    private Context context;
    private EventBus eventBus;
    private UserStore userStore;

    public InviteUserViewModel(Context context, EventBus eventBus, UserStore userStore) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
    }

}
