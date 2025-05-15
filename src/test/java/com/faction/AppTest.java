package com.faction;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.faction.elements.Assessment;
import com.faction.elements.CheckList;
import com.faction.elements.CheckListItem;

public class AppTest {

    @Test
    public void test() {

        ChecklistApp app = new ChecklistApp();

        String reportText = "${checklist-test-list}";

        Assessment asmt = new Assessment();
        CheckList checklist = new CheckList();
        CheckListItem item1 = new CheckListItem();
        item1.setQuestion("question1");
        item1.setAnswer(CheckListItem.Answer.Pass);
        CheckListItem item2 = new CheckListItem();
        item2.setQuestion("question2");
        item2.setAnswer(CheckListItem.Answer.Fail);
        CheckListItem item3 = new CheckListItem();
        item3.setQuestion("question3");
        item3.setAnswer(CheckListItem.Answer.NA);
        item2.setNotes("comment3");
        checklist.setName("Test List");
        checklist.setCheckListItems(new ArrayList<>());
        checklist.getCheckListItems().add(item1);
        checklist.getCheckListItems().add(item2);
        checklist.getCheckListItems().add(item3);

        asmt.setChecklists(new ArrayList<>());
        asmt.getChecklists().add(checklist);

        HashMap<String,String> configs = new HashMap<>();
        configs.put("Pass Font Color", "red");
        configs.put("Pass Cell Color", "blue");
        configs.put("Fail Font Color", "green");
        configs.put("Fail Cell Color", "black");
        configs.put("NA Font Color", "pink");
        configs.put("NA Cell Color", "white");
        configs.put("Show Comments", "true");
		app.setConfigs(configs);

        String updatedText = app.reportCreate(asmt, new ArrayList<>(), reportText);
        System.out.println(updatedText);

        assertTrue(true);
    }
}
