import java.io.Serializable;
import java.util.*;

/**
 * Desciption.
 * * @author Banana Monster
 *
 */


class AppState implements Serializable {
    private static final long serialVersionUID = 1L;
    LinkedList<HealthProfessional> professionals = new LinkedList<>();
    HashMap<String, Diary> diaries = new HashMap<>();
    HashMap<String, LinkedList<Task>> taskLists = new HashMap<>();

    void ensureStructures(String profId) {
        diaries.computeIfAbsent(profId, k -> new Diary());
        taskLists.computeIfAbsent(profId, k -> new LinkedList<>());
    }
}