package me.redth.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.GuiIngameForge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardHud extends BaseHud {
    public static final ScoreObjective PLACEHOLDER = new ScoreObjective(new Scoreboard(), "NotEnoughHUDs", IScoreObjectiveCriteria.DUMMY);
    public static final List<Score> DEFAULT_SCORES = ImmutableList.of(new Score(PLACEHOLDER.getScoreboard(), PLACEHOLDER, "Steve"), new Score(PLACEHOLDER.getScoreboard(), PLACEHOLDER, "Alex"));
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showNumbers = new NehBoolean("show_numbers", true);
    public final NehColor numberColor = new NehColor("number_color", "FFFF5555");
    public final NehBoolean ascending = new NehBoolean("ascending", false);
    public final NehInteger maxEntries = new NehInteger("max_entries", 15, 5, 30);
    private static ScoreObjective objective = null;
    private static List<Score> scores = Collections.emptyList();

    public ScoreboardHud() {
        super("scoreboard");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showNumbers);
        options.add(numberColor);
        options.add(ascending);
        options.add(maxEntries);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        GuiIngameForge.renderObjective = !enabled;
    }

    @Override
    public void tick() {
        objective = getObjective();
        scores = getScores();
        super.tick();
    }

    public static ScoreObjective getObjective() {
        ScoreObjective obj = null;
        try {
            Scoreboard sb = mc.theWorld.getScoreboard();
            ScorePlayerTeam spt = sb.getPlayersTeam(mc.thePlayer.getName());
            if (spt != null) {
                int slot = spt.getChatFormat().getColorIndex();
                if (slot >= 0) obj = sb.getObjectiveInDisplaySlot(3 + slot);
            }
            if (obj == null) obj = sb.getObjectiveInDisplaySlot(1);
            if (obj.getScoreboard().getSortedScores(obj).isEmpty()) obj = null;
        } catch (Throwable e) {
            obj = null;
        }
        return (obj == null && isEditing()) ? PLACEHOLDER : obj;
    }

    public List<Score> getScores() {
        if (objective == null) return Collections.emptyList();
        if (PLACEHOLDER.equals(objective)) return DEFAULT_SCORES;

        List<Score> sorted = (List<Score>) objective.getScoreboard().getSortedScores(objective);
        if (!ascending.get()) Collections.reverse(sorted);

        List<Score> list = new ArrayList<>(maxEntries.get());
        for (Score s : sorted) {
            if (s.getPlayerName() != null && !s.getPlayerName().startsWith("#"))
                list.add(s);
            if (list.size() >= maxEntries.get()) break;
        }

//        List<Score> list = objective.getScoreboard().getSortedScores(objective).stream()
//                .filter(s -> s.getPlayerName() != null && !s.getPlayerName().startsWith("#"))
//                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void render() {
        if (objective == null) return;
        drawBg(backgroundColor);

        drawString(objective.getDisplayName(), width / 2.0F, 1, 0xFFFFFF, textShadow.get(), Alignment.CENTER);

        int y = 10;

        Scoreboard sb = objective.getScoreboard();
        for (Score score : scores) {
            ScorePlayerTeam team = sb.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            drawString(s, 2, y, 0xFFFFFF, textShadow.get());

            if (showNumbers.get()) {
                drawString(String.valueOf(score.getScorePoints()), width - 2, y, numberColor.asInt(), textShadow.get(), Alignment.RIGHT);
            }

            y += 9;
        }
    }

    @Override
    protected int getWidth() {
        if (objective == null) return 0;
        int i = mc.fontRendererObj.getStringWidth(objective.getDisplayName());
        for (Score score : scores) {
            ScorePlayerTeam spt = objective.getScoreboard().getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(spt, score.getPlayerName());
            if (showNumbers.get()) s += ": " + score.getScorePoints();
            int j = mc.fontRendererObj.getStringWidth(s);
            if (i < j) i = j;
        }
        return i + 4;
    }

    @Override
    protected int getHeight() {
        if (objective == null) return 0;
        return scores.size() * mc.fontRendererObj.FONT_HEIGHT + mc.fontRendererObj.FONT_HEIGHT + 2;
    }
}
