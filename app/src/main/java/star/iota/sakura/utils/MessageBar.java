package star.iota.sakura.utils;

import android.app.Activity;
import android.content.Context;

import com.liuguangqiang.cookie.CookieBar;
import com.liuguangqiang.cookie.OnActionClickListener;

import java.util.Random;


public class MessageBar {
    public static final String[] FACES = {
            "ヽ(✿ﾟ▽ﾟ)ノ", "━━(￣ー￣*|||━━", "┗|*｀0′*|┛", "o(*^▽^*)┛",
            "( σ'ω')σ", "✧(≖ ◡ ≖✿)", "|(•_•) |•_•) |_•) |•) | )", "(ﾟｰﾟ)",
            "´･∀･)乂(･∀･｀", "(。・∀・)ノ", "(oﾟvﾟ)ノ", "(*ﾟｰﾟ)",
            "ʅ（´◔౪◔）ʃ", "(‾◡◝)", "(☆-ｖ-)", "*(੭*ˊᵕˋ)੭*ଘ",
            "(>▽<)", "┗|｀O′|┛ 嗷~~", "<(￣3￣)> 表！", "不＞(￣ε￣ = ￣3￣)<要",
            "(≧∇≦)ﾉ", "～(　TロT)σ", "n(*≧▽≦*)n", "（*＾-＾*）",
            "（○｀ 3′○）", "（○｀ 3′○）", "( *￣▽￣)((≧︶≦*)", "(っ*´Д`)っ",
            "ο(=•ω＜=)ρ⌒☆", "ヾ(´･ω･｀)ﾉ", "ヾ(^▽^*)))", "ｍ(o・ω・o)ｍ",
            "ε = = (づ′▽`)づ", "(ノω<。)ノ))☆.。", "(。・・)ノ", "(/ω＼*)……… (/ω•＼*)",
            "┬┴┤_•)", "（o´・ェ・｀o）", "(#`O′)", "o(〃'▽'〃)o",
            "( ╯▽╰)", "(～o￣3￣)～", "(*￣3￣)╭", "（づ￣3￣）づ╭❤～",
            "(。﹏。)", "(/▽＼)", "(′▽`〃)", "◕ฺ‿◕ฺ✿ฺ)",
            "(*/ω＼*)", "(๑•̀ㅂ•́)و✧", "ヾ(≧▽≦*)o", "o(*≧▽≦)ツ",
            "<(￣︶￣)>", "︿(￣︶￣)︿", "嗯~ o(*￣▽￣*)o", "╰(￣▽￣)╭",
            "(｡･∀･)ﾉﾞ", "ヾ(≧∇≦*)ゝ", "(*^▽^*)", "╰(￣▽￣)╭",
            "（゜▽＾*））", "ヽ(✿ﾟ▽ﾟ)ノ", "(( へ(へ´∀`)へ", "╰(*°▽°*)╯",
            "^O^", "♪(^∇^*)", "(≧∀≦)ゞ", "(๑´ㅂ`๑)",
            "(๑¯∀¯๑)", "(/≧▽≦)/", "ヽ(ﾟ∀ﾟ*)ﾉ━━━ｩ♪", "o(*≧▽≦)ツ┏━┓",
            "ε(*´･∀･｀)зﾞ", "~(～￣▽￣)～", "(o゜▽゜)o☆", "o(*￣▽￣*)o", "(〃ﾉωﾉ)"
    };

    public static void create(Context context, String content) {
        new CookieBar.Builder((Activity) context)
                .setTitle(FACES[new Random().nextInt(FACES.length)])
                .setMessage(content)
                .show();
    }

    public static void create(Context context, String content, String action, OnActionClickListener onActionClickListener) {
        new CookieBar.Builder((Activity) context)
                .setTitle(FACES[new Random().nextInt(FACES.length)])
                .setMessage(content)
                .setAction(action, onActionClickListener)
                .show();
    }
}
