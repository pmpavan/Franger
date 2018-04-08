package com.frangerapp.franger.app.util.di.component.user;

import com.frangerapp.franger.app.util.di.module.user.UserModule;
import com.frangerapp.franger.app.util.di.module.user.chat.ChatModule;
import com.frangerapp.franger.app.util.di.module.user.contact.ContactModule;
import com.frangerapp.franger.app.util.di.module.user.home.HomeModule;
import com.frangerapp.franger.app.util.di.module.user.home.IncomingListModule;
import com.frangerapp.franger.app.util.di.module.user.home.OutgoingListModule;
import com.frangerapp.franger.app.util.di.module.user.invite.InviteModule;
import com.frangerapp.franger.app.util.di.module.user.profile.ProfileModule;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.ui.chat.ChatActivity;
import com.frangerapp.franger.ui.home.IncomingFragment;
import com.frangerapp.franger.ui.home.OutgoingFragment;
import com.frangerapp.franger.ui.contact.ContactActivity;
import com.frangerapp.franger.ui.home.HomeActivity;
import com.frangerapp.franger.ui.invite.InviteActivity;
import com.frangerapp.franger.ui.profile.AddEditProfileActivity;

import dagger.Subcomponent;

/**
 * Created by Pavan on 24/01/18.
 */
@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {


    HomeComponent plus(HomeModule module);

    AddEditProfileComponent plus(ProfileModule module);

    InviteComponent plus(InviteModule module);

    ContactComponent plus(ContactModule module);

    ChatComponent plus(ChatModule module);

    OutgoingListComponent plus(OutgoingListModule module);

    IncomingListComponent plus(IncomingListModule module);

    /**
     * HOME
     */
    @ActivityScope
    @Subcomponent(modules = HomeModule.class)
    interface HomeComponent {
        void inject(HomeActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = ProfileModule.class)
    interface AddEditProfileComponent {
        void inject(AddEditProfileActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = InviteModule.class)
    interface InviteComponent {
        void inject(InviteActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = ContactModule.class)
    interface ContactComponent {
        void inject(ContactActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = ChatModule.class)
    interface ChatComponent {
        void inject(ChatActivity activity);
    }

    @FragmentScope
    @Subcomponent(modules = OutgoingListModule.class)
    interface OutgoingListComponent {
        void inject(OutgoingFragment activity);
    }

    @FragmentScope
    @Subcomponent(modules = IncomingListModule.class)
    interface IncomingListComponent {
        void inject(IncomingFragment activity);
    }


}
