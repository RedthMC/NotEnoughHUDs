package io.github.redth.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardHud extends BaseHud {
    public static final ScoreboardObjective PLACEHOLDER = new ScoreboardObjective(new Scoreboard(), "NotEnoughHUDs", ScoreboardCriterion.DUMMY, Text.of("\u00a7bNotEnoughHUDs"), ScoreboardCriterion.RenderType.INTEGER);
    public static final List<ScoreboardPlayerScore> DEFAULT_SCORES = ImmutableList.of(new ScoreboardPlayerScore(PLACEHOLDER.getScoreboard(), PLACEHOLDER, "Steve"), new ScoreboardPlayerScore(PLACEHOLDER.getScoreboard(), PLACEHOLDER, "Alex"));
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showNumbers = new NehBoolean("show_numbers", true);
    public final NehColor numberColor = new NehColor("number_color", "FFFF5555");
    private static ScoreboardObjective objective = null;
    private static List<ScoreboardPlayerScore> scores = Collections.emptyList();

    public ScoreboardHud() {
        super("scoreboard");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showNumbers);
        options.add(numberColor);
    }

    @Override
    public void tick() {
        objective = getObjective();
        scores = getScores();
        super.tick();
    }

    public static ScoreboardObjective getObjective() {
        ScoreboardObjective obj = null;
        try {
            Scoreboard sb = mc.world.getScoreboard();
            Team team = sb.getPlayerTeam(mc.player.getEntityName());
            if (team != null) {
                int i = team.getColor().getColorIndex();
                if (i >= 0) {
                    obj = sb.getObjectiveForSlot(3 + i);
                }
            }
            if (obj == null) obj = sb.getObjectiveForSlot(1);
            if (obj.getScoreboard().getAllPlayerScores(obj).isEmpty()) obj = null;
        } catch (NullPointerException ignored) {
        }
        return (obj == null && isEditing()) ? PLACEHOLDER : obj;
    }

    public static List<ScoreboardPlayerScore> getScores() {
        if (objective == null) return Collections.emptyList();
        if (PLACEHOLDER.equals(objective)) return DEFAULT_SCORES;
        List<ScoreboardPlayerScore> list = new ArrayList<>();
        for (ScoreboardPlayerScore score : objective.getScoreboard().getAllPlayerScores(objective)) {
            if (list.size() >= 15) break;
            if (score.getPlayerName() != null && !score.getPlayerName().startsWith("#")) {
                list.add(score);
            }
        }
        return list;
    }

    @Override
    public void render(MatrixStack matrix) {
        if (objective == null) return;
        drawBg(matrix, backgroundColor);
        int y = getHeight() - 9;
        int width = getWidth();
        Scoreboard sb = objective.getScoreboard();

        for (ScoreboardPlayerScore score : scores) {
            Team team = sb.getPlayerTeam(score.getPlayerName());
            Text text = Team.decorateName(team, Text.of(score.getPlayerName()));
            drawText(matrix, text, 2, y, 0xFFFFFF, textShadow.get());

            if (showNumbers.get()) {
                drawString(matrix, String.valueOf(score.getScore()), width - 2, y, numberColor.asColor(), textShadow.get(), Alignment.RIGHT);
            }

            y -= 9;
        }

        drawText(matrix, objective.getDisplayName(), width / 2.0F, y, 0xFFFFFF, textShadow.get(), Alignment.CENTER);
    }

    @Override
    protected int getWidth() {
        if (objective == null) return 0;
        int i = mc.textRenderer.getWidth(objective.getDisplayName());
        for (ScoreboardPlayerScore score : scores) {
            Team team = objective.getScoreboard().getPlayerTeam(score.getPlayerName());
            Text text = Team.decorateName(team, Text.of(score.getPlayerName()));
            int j = mc.textRenderer.getWidth(text);
            if (showNumbers.get()) j += mc.textRenderer.getWidth(": " + score.getScore());
            if (i < j) i = j;
        }
        return i + 4;
    }

    @Override
    protected int getHeight() {
        if (objective == null) return 0;
        return scores.size() * 9 + 11;
    }
}
