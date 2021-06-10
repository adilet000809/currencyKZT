import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private static final String START_COMMAND = "/start";
    private static final String GET_INFO_COMMAND = "get_info";
    private static final String ERROR_MESSAGE = "Couldn't fetch data.";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public SendMessage sendButton(long id){
        SendMessage message = new SendMessage();
        message.setChatId(id);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(getKeyBoardButton());
        message.setReplyMarkup(markupInline);
        message.setText("Press the button to get info");
        return message;
    }

    private List<List<InlineKeyboardButton>> getKeyBoardButton() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Get info").setCallbackData(GET_INFO_COMMAND));
        rowsInline.add(rowInline);
        return rowsInline;
    }

    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            if(update.getMessage().getText().equals(START_COMMAND)){
                try {
                    execute(sendButton(update.getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if(update.hasCallbackQuery()) {
            if(update.getCallbackQuery().getData().equals(GET_INFO_COMMAND)){
                List<Rate> rates = Currency.getRates();
                if (rates != null) {
                    StringBuilder result = new StringBuilder();
                    rates.forEach(rate -> result.append(rate.getTitle()).append(" : ").append(rate.getDescription()).append("\n"));
                    EditMessageText newMessage = getEditMessageText(
                            update.getCallbackQuery().getMessage().getChatId(),
                            update.getCallbackQuery().getMessage().getMessageId(),
                            result.toString());
                    try {
                        execute(newMessage);
                        execute(sendButton(update.getCallbackQuery().getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        EditMessageText errorMessage = getEditMessageText(
                                update.getCallbackQuery().getMessage().getChatId(),
                                update.getCallbackQuery().getMessage().getMessageId(),
                                ERROR_MESSAGE);
                        execute(errorMessage);
                        execute(sendButton(update.getCallbackQuery().getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private EditMessageText getEditMessageText(long chatId, int messageId, String message) {
        return new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(message);
    }

    public String getBotUsername() {
        return "currency_kz_bot";
    }

    public String getBotToken() {
        return "1700501549:AAEywmX_0ivU5y9Qg_ZhcawNj4Ha9GbYD3o";
    }

}
