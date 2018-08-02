package ru.savkin;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class AdminBot extends TelegramLongPollingBot {

    private static AdminBot bot;

    private AdminBot() {

    }

    public static AdminBot getBot() {
        if (bot == null) {
            bot = new AdminBot();

        }
        return bot;
    }


    public void onUpdateReceived(Update update) {

    }


    public String getBotUsername() {
        return "newcustomerphonebot";
    }


    public String getBotToken() {
        return "537073902:AAFKz00HwQ4bWwjHtrqSzftBbLNQzAit0BQ";
    }

    public void sendMsg(Message msg, String text) {
        SendMessage sm = new SendMessage()
                .setChatId("331660513")
                .setText(text).enableHtml(true);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message msg, String text, InlineKeyboardMarkup keyboardMarkup) {

        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(msg.getChatId())
                .setText(text);
        message.setReplyMarkup(keyboardMarkup);
        message.setParseMode(ParseMode.HTML);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
