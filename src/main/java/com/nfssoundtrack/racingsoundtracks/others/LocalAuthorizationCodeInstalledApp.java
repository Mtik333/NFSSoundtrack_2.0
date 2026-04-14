package com.nfssoundtrack.racingsoundtracks.others;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.IOException;

public class LocalAuthorizationCodeInstalledApp extends AuthorizationCodeInstalledApp {

    private String adminId;
    private String botSecret;

    public LocalAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver,
                                              String adminId, String botSecret) {
        super(flow, receiver);
        this.adminId = adminId;
        this.botSecret = botSecret;
    }

    @Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        try{
            String url = authorizationUrl.build();
            WebsiteViewsController.rebuildJda(botSecret);
            RestAction<Member> memberRestAction = WebsiteViewsController.getJda().getGuilds().get(0).retrieveMemberById(adminId);
            memberRestAction.queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Please open the following address in your browser:" + url).queue());
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.onAuthorization(authorizationUrl);
    }
}
