package savkin;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {


    private HashMap<Long, Order> orderMap;
    private final DataReader dataReader;
    private final SimpleDateFormat formatForDateNow;
    private final Date dateNow;
    private final String OTHER_DATA = "Другая дата";
    private final  List<String> samokatsList;

    private final Object obj = new Object();


    public Bot() {

        dateNow = new Date();
        formatForDateNow = new SimpleDateFormat("dd.MM.yy");
        dataReader = new DataReader();
        samokatsList  = dataReader.getTariffis();
        orderMap = new HashMap<>();



    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void onUpdateReceived(Update update) {
        System.out.println("Po");
        if (update.hasMessage()) {
            if(update.getMessage().getContact() != null) {
                   makeOrder(update);
            }

        }
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                System.out.println(update.getMessage());
                getAvailableSamokats(update);
                Order   order = new Order();
                orderMap.put(chat_id, order);
            }

        }

        if (update.hasCallbackQuery()) {

            String msg = update.getCallbackQuery().getData();
            if (dataChose(update, msg)) {
                    requestPhoneNumber(update,  msg);

            }
            if (msg.equals(OTHER_DATA)) {
                sendMsg(update.getCallbackQuery().getMessage(), "Запрос передан");
                AdminBot.getBot().sendMsg(update.getCallbackQuery().getMessage(), "Пользователь  @" + update.getCallbackQuery().getFrom().getUserName() + " хочет в другое время");

            }
        }
    }

    private void requestPhoneNumber(Update update, String msg) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setText("Скажите нам номер телефона");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Отправить свой номер телефона").setRequestContact(true);
        keyboardFirstRow.add(keyboardButton);
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getAvailableSamokats(Update update) {
        int countData = 6;
        String [][]samokats = new String[countData][1];
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateNow);
        int i = 0;
        int incrementData = 1;
        while(i < countData - 1) {
            cal.add(Calendar.DATE,  incrementData); //minus number would decrement the days
            String formatted = formatForDateNow.format(cal.getTime());
            System.out.println(formatted);
            if (!samokatsList.contains(formatted)) {
                samokats[i][0] = (i + 1) + ". " +  formatted;
                i++;
            }
        }
        samokats[samokats.length - 1][0] = countData + ". "  + OTHER_DATA;
        sendMsgWithPhoto(update.getMessage(), "Даты,  когда есть свободные самокаты", getKeyBoard(samokats));
    }
    public String getBotUsername() {
        return "Testbotsav_bot";
    }

    private void makeOrder(Update update) {
        sendMsg(update.getMessage(), "Спасибо. Наша команда сейчас позвонит Вам");
        AdminBot.getBot().sendMsg(update.getMessage(),
                        "Заказ самоката от @"
                        + update.getMessage().getFrom().getUserName()
                                +  "\nТелефон +" +
                        update.getMessage().getContact().getPhoneNumber() +
                                "\nДата "
                                + orderMap.get(update.getMessage().getChatId()).getData()
        );
    }

    public String getBotToken() {
        return "TOKEN";
    }



    private synchronized boolean dataChose(Update update, String messageText) {
        final String regex = "([0-9]{1,2}.\\s?)+";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(messageText);
        if (matcher.find()) {
            synchronized (obj) {
                orderMap.get(update.getCallbackQuery().getMessage().getChatId()).setData(matcher.group());
                System.out.println("Группа " + matcher.group());
                // orderMap.get(update.getCallbackQuery().getMessage().getChatId()).setNumber(matcher.group());
                samokatsList.remove(messageText);
                return true;
            }
        }
        return false;

    }

    private InlineKeyboardMarkup getKeyBoard(String [][] s) {
        List<List<InlineKeyboardButton>> list = new ArrayList();
        for (String [] row: s) {
            List<InlineKeyboardButton> inlin = getKeyboardRow(row);
            if (inlin == null) {
                throw new NullPointerException("No row");
            }
            list.add(inlin);
        }

        if (list.isEmpty())
            throw   new NullPointerException("No list");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(list);
        return  markupInline;
    }

    private List<InlineKeyboardButton>  getKeyboardRow(String ... s) {
        List<InlineKeyboardButton> rowInline = new ArrayList();
        for (String button: s) {
            rowInline.add(new InlineKeyboardButton().setText(button).setCallbackData(button));
        }
        return rowInline;
    }

    private void sendMsg(Message msg, String text) {
        SendMessage sm = new SendMessage()
                .setChatId(msg.getChatId())
                .setText(text).enableHtml(true);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsgWithPhoto(Message msg, String text,InlineKeyboardMarkup keyboardMarkup) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(msg.getChatId());
        sendPhotoRequest.setPhoto("http://dsavkin.ru/wp-content/uploads/2018/07/samokat.jpg");
        sendPhotoRequest.setCaption(text);
        sendPhotoRequest.setReplyMarkup(keyboardMarkup);

        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void sendMsg(Message msg, String text, InlineKeyboardMarkup keyboardMarkup) {

        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(msg.getChatId())
                .setText(text)
                ;
        message.setReplyMarkup(keyboardMarkup);
        message.setParseMode(ParseMode.HTML);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }




}
