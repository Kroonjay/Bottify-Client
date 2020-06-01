package utils.event;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.listener.LoginResponseCodeListener;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class LoginEvent extends Event implements LoginResponseCodeListener {
    public enum LoginEventResult {
        UNEXPECTED_SERVER_ERROR(1, "Unexpected server error"),
        LOG_IN(2, "Log in"),
        INVALID_CREDENTIALS(3, "Invalid username or password"),
        BANNED(4, "Username is banned"),
        ACCOUNT_ALREADY_LOGGED_IN(5, "Account is already logged in try again in 60 seconds"),
        RUNESCAPE_UPDATED(6, "Runescape has been updated! Please reload this page."),
        WORLD_IS_FULL(7, "This world is full. Please use a different world."),
        LOGIN_SERVER_OFFLINE(8, "Unable to connect. login server offline."),
        TOO_MANY_CONNECTIONS_FROM_ADDRESS(9, "Login limit exceeded. Too many connections from you address."),
        BAD_SESSION_ID(10, "Unable to connect. Bad session id."),
        PASSWORD_CHANGE_REQUIRED(11, "We suspect someone knows your password. Press 'change your password' on the front page."),
        MEMBERS_ACCOUNT_REQUIRED(12, "You need a members account to login to this world. Please subscribe, or use a different world."),
        TRY_DIFFERENT_WORLD(13, "Could not complete login. Please try using a different world."),
        TRY_AGAIN(14, "The server is being updated. Please wait 1 minute and try again."),
        SERVER_UPDATE(15, "The server is being updated. Please wait 1 minute and try again."),
        TOO_MANY_INCORRECT_LOGINS(16, "Too many incorrect longs from your address. Please wait 5 minutes before trying again."),
        STANDING_IN_MEMBERS_ONLY_AREA(17, "You are standing in a members-only area. To play on this world move to a free area first."),
        ACCOUNT_LOCKED(18, "Account locked as we suspect it has been stolen. Press 'recover a locked account' on front page."),
        CLOSED_BETA(19, "This world is running a closed beta. sorry invited players only. please use a different world."),
        INVALID_LOGIN_SERVER(20, "Invalid loginserver requested please try using a different world."),
        PROFILE_WILL_BE_TRANSFERRED(21, "You have only just left another world. your profile will be transferred in 4seconds."),
        MALFORMED_LOGIN_PACKET(22, "Malformed login packet. Please try again"),
        NO_REPLY_FROM_LOGIN_SERVER(23, "No reply from loginserver. Please wait 1 minute and try again."),
        ERROR_LOADING_PROFILE(24, "Error loading your profile. please contact customer support."),
        UNEXPECTED_LOGIN_SERVER_RESPONSE(25, "Unexepected loginserver response"),
        COMPUTER_ADDRESS_BANNED(26, "This computers address has been blocked as it was used to break our rules."),
        SERVICE_UNAVAILABLE(27, "Service unavailable.");

        int code;
        String message;

        LoginEventResult(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private static final Map<Integer, LoginEventResult> responseCodeLoginResultMap = new HashMap<>();
    static {
        for (LoginEventResult result : LoginEventResult.values()) {
            responseCodeLoginResultMap.put(result.code, result);
        }
    }

    private static final Rectangle TRY_AGAIN_BUTTON = new Rectangle(318, 262, 130, 26);
    private static final Rectangle LOGIN_BUTTON = new Rectangle(240, 310, 120, 20);
    private static final Rectangle EXISTING_USER_BUTTON = new Rectangle(400, 280, 120, 20);
    private static final Rectangle CANCEL_LOGIN_BUTTON = new Rectangle(398, 308, 126, 27);
    private static final Rectangle CANCEL_WORLD_SELECTOR_BUTTON = new Rectangle(712, 8, 42, 8);

    private final String username, password;
    private int maxRetries = 5;

    private LoginEventResult loginEventResult;
    private int retryNumber = 0;

    public LoginEvent(final String username, final String password) {
        this.username = username;
        this.password = password;

        setAsync();
    }

    public LoginEvent(final String username, final String password, final int maxRetries) {
        this(username, password);
        this.maxRetries = maxRetries;
    }

    @Override
    public final int execute() throws InterruptedException {
        if (loginEventResult != null) {
            handleLoginResponse();
        }

        if (retryNumber >= maxRetries) {
            setFailed();
        }

        if (hasFailed()) {
            return 0;
        }

        if (!getBot().isLoaded()) {
            return 1000;
        } else if (getClient().isLoggedIn() && getLobbyButton() == null) {
            setFinished();
            return 0;
        }

        if (getLobbyButton() != null) {
            clickLobbyButton();
        } else if (isOnWorldSelectorScreen()) {
            cancelWorldSelection();
        } else if (!isPasswordEmpty()) {
            clickButton(CANCEL_LOGIN_BUTTON);
        } else {
            login();
        }
        return random(100, 150);
    }

    public LoginEventResult getLoginEventResult() {
        return loginEventResult;
    }

    private void handleLoginResponse() throws InterruptedException {
        switch (loginEventResult) {
            case BANNED:
            case PASSWORD_CHANGE_REQUIRED:
            case ACCOUNT_LOCKED:
            case COMPUTER_ADDRESS_BANNED:
            case UNEXPECTED_SERVER_ERROR:
            case INVALID_CREDENTIALS:
            case RUNESCAPE_UPDATED:
            case LOGIN_SERVER_OFFLINE:
            case TOO_MANY_CONNECTIONS_FROM_ADDRESS:
            case BAD_SESSION_ID:
            case UNEXPECTED_LOGIN_SERVER_RESPONSE:
            case SERVICE_UNAVAILABLE:
            case TOO_MANY_INCORRECT_LOGINS:
            case ERROR_LOADING_PROFILE:
                setFailed();
                break;
            case ACCOUNT_ALREADY_LOGGED_IN:
            case TRY_AGAIN:
            case SERVER_UPDATE:
            case NO_REPLY_FROM_LOGIN_SERVER:
            case MALFORMED_LOGIN_PACKET:
                sleep(random((int)TimeUnit.MINUTES.toMillis(1), (int)TimeUnit.MINUTES.toMillis(2)));
                retryNumber++;
                break;
            case PROFILE_WILL_BE_TRANSFERRED:
                sleep(random((int)TimeUnit.SECONDS.toMillis(5),(int)TimeUnit.SECONDS.toMillis(10)));
                retryNumber++;
                break;
            case WORLD_IS_FULL:
            case TRY_DIFFERENT_WORLD:
            case CLOSED_BETA:
            case INVALID_LOGIN_SERVER:
            case MEMBERS_ACCOUNT_REQUIRED:
            case STANDING_IN_MEMBERS_ONLY_AREA:
                // Should hop to a different world here
                setFailed();
                break;
        }
    }

    private boolean isOnWorldSelectorScreen() {
        return getColorPicker().isColorAt(50, 50, Color.BLACK);
    }

    private void cancelWorldSelection() {
        if (clickButton(CANCEL_WORLD_SELECTOR_BUTTON)) {
            new ConditionalSleep(3000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !isOnWorldSelectorScreen();
                }
            }.sleep();
        }
    }

    private boolean isPasswordEmpty() {
        return !getColorPicker().isColorAt(350, 260, Color.WHITE);
    }

    private void login() {
        switch (getClient().getLoginUIState()) {
            case 0:
                clickButton(EXISTING_USER_BUTTON);
                break;
            case 1:
                clickButton(LOGIN_BUTTON);
                break;
            case 2:
                enterUserDetails();
                break;
            case 3:
                clickButton(TRY_AGAIN_BUTTON);
                break;
        }
    }

    private void enterUserDetails() {
        if (!getKeyboard().typeString(username)) {
            return;
        }

        if (!getKeyboard().typeString(password)) {
            return;
        }

        new ConditionalSleep(30_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return getLobbyButton() != null ||
                       getClient().isLoggedIn() ||
                       getClient().getLoginUIState() == 3 ||
                       loginEventResult == LoginEventResult.BANNED ||
                       loginEventResult == LoginEventResult.ACCOUNT_LOCKED;
            }
        }.sleep();
    }

    private void clickLobbyButton() {
        if (getLobbyButton() != null && getLobbyButton().interact()) {
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return getLobbyButton() == null;
                }
            }.sleep();
        }
    }

    private RS2Widget getLobbyButton() {
        try {
            return getWidgets().getWidgetContainingText("CLICK HERE TO PLAY");
        } catch (NullPointerException e) {
            return null;
        }
    }

    private boolean clickButton(final Rectangle rectangle) {
        return getMouse().click(new RectangleDestination(getBot(), rectangle));
    }

    @Override
    public final void onResponseCode(final int responseCode) {
        if (!responseCodeLoginResultMap.containsKey(responseCode)) {
            log("Got unknown login response code " + responseCode);
            setFailed();
            return;
        }

        this.loginEventResult = responseCodeLoginResultMap.get(responseCode);
        log(String.format("Got login response: %d '%s'", responseCode, loginEventResult.message));
    }
}