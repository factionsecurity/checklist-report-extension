package com.faction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.faction.elements.Assessment;
import com.faction.elements.BaseExtension;
import com.faction.elements.CheckList;
import com.faction.elements.CheckListItem;
import com.faction.elements.Vulnerability;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;

public class ChecklistApp extends BaseExtension implements com.faction.extender.ReportManager {

    @Override
    public String reportCreate(Assessment assessment, List<Vulnerability> vulns, String reportText) {
        for (CheckList checklist : assessment.getChecklists()) {
            String variable = "\\$\\{checklist-";
            variable += checklist.getName().toLowerCase().replaceAll(" ", "-");
            variable += "\\}";
            reportText = reportText.replaceAll(variable, this.addCheckList(checklist));
        }
        return reportText;
    }

    private String addCheckList(CheckList checklist){
        Context context = Context
				  .newBuilder(checklist.getCheckListItems())
				  .resolver(JavaBeanValueResolver.INSTANCE)
				  .build();
		Template template = this.setupHandlebars();
		try {
			return template.apply(context);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
    }

    /**
     * This section covers everything thats needed to generate the report sections
     * 
     */
    private Template setupHandlebars() {
        try {
            Handlebars handlebars = new Handlebars();
            String passFont = this.getConfigs().get("Pass Font Color");
            String passBg = this.getConfigs().get("Pass Cell Color");
            String failFont = this.getConfigs().get("Fail Font Color");
            String failBg = this.getConfigs().get("Fail Cell Color");
            String naFont = this.getConfigs().get("NA Font Color");
            String naBg = this.getConfigs().get("NA Cell Color");

            handlebars.registerHelper("status", new Helper<CheckListItem>() {
                @Override
                public Object apply(CheckListItem context, Options options) throws IOException {
                    if (context.getAnswer().equals(CheckListItem.Answer.Pass)) {
                        return "PASS";
                    } else if (context.getAnswer().equals(CheckListItem.Answer.Fail)) {
                        return "FAIL";
                    } else if (context.getAnswer().equals(CheckListItem.Answer.NA)) {
                        return "N/A";
                    } else {
                        return "";
                    }
                }
            });
            handlebars.registerHelper("color", new Helper<CheckListItem>() {
                @Override
                public Object apply(CheckListItem context, Options options) throws IOException {
                    if (context.getAnswer().equals(CheckListItem.Answer.Pass)) {
                        return passFont;
                    } else if (context.getAnswer().equals(CheckListItem.Answer.Fail)) {
                        return failFont;
                    } else if (context.getAnswer().equals(CheckListItem.Answer.NA)) {
                        return naFont;
                    } else {
                        return "";
                    }
                }
            });
            handlebars.registerHelper("background", new Helper<CheckListItem>() {
                @Override
                public Object apply(CheckListItem context, Options options) throws IOException {
                    if (context.getAnswer().equals(CheckListItem.Answer.Pass)) {
                        return passBg;
                    } else if (context.getAnswer().equals(CheckListItem.Answer.Fail)) {
                        return failBg;
                    } else if (context.getAnswer().equals(CheckListItem.Answer.NA)) {
                        return naBg;
                    } else {
                        return "";
                    }
                }
            });
            handlebars.registerHelper("comment", new Helper<CheckListItem>() {
                @Override
                public Object apply(CheckListItem context, Options options) throws IOException {
                    return context.getNotes();
                }
            });

            String showComments = this.getConfigs().get("Show Comments");
            String templatePath ="META-INF/resources/checklist-table-wo-comments.hbs";
            if(showComments != null && showComments.equals("true")){
                templatePath ="META-INF/resources/checklist-table.hbs";
            }

            BufferedReader txtReader = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(templatePath)));
            String templateString = "";
            for (String line; (line = txtReader.readLine()) != null;) {
                templateString += line;
            }

            Template template;
            template = handlebars.compileInline(templateString);
            return template;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
