package com.imjustdoom.doomsjde.api.data.objects.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectTab {

    @JsonProperty("project_name")
    public String projectName = "";

    @JsonProperty("project_path")
    public String projectPath = "";

}