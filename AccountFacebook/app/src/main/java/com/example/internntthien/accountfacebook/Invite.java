package com.example.internntthien.accountfacebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import bolts.AppLinks;


public class Invite extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AppInviteDialog mInvititeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appLinkUrl, previewImageUrl;
        FacebookSdk.sdkInitialize(this);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        } else {
            AppLinkData.fetchDeferredAppLinkData( this,
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //process applink data
                        }
                    });
        }

        appLinkUrl = "https://fb.me/427103474355624";
        previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

        callbackManager = CallbackManager.Factory.create();

        mInvititeDialog = new AppInviteDialog(this);
        mInvititeDialog.registerCallback(callbackManager,
                new FacebookCallback<AppInviteDialog.Result>(){

                    @Override
                    public void onSuccess(AppInviteDialog.Result result) {
                        Toast.makeText(Invite.this,"Invitation Sent Successfully!"
                                , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("Result", "Cancelled");
                        Toast.makeText(Invite.this,"Cancelled"
                                , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("Result", "Error " + exception.getMessage());
                        Toast.makeText(Invite.this,"Error while inviting friends"
                                , Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
