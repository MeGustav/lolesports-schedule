<#-- Aliases of main events -->
<#assign mainEvents = [
    "quarterfinal",
    "semifinal",
    "grand-final",
    "third-place"
]/>

<#-- Teams competing in a single match -->
<#assign MATCH_TEAMS = 2/>

<#-- Forms the teams part of match -->
<#function formTeamsPart teams>
    <#assign preparedTeams = []/>
    <#list teams as team>
        <#assign preparedTeams = preparedTeams + ["*" + team + "*"]/>
    </#list>
    <#assign size = preparedTeams?size/>
    <#if size != MATCH_TEAMS>
        <#list size..MATCH_TEAMS - 1 as idx>
            <#assign preparedTeams = preparedTeams + ["_[TBD]_"]/>
        </#list>
    </#if>
    <#return preparedTeams?join(" vs ")/>
</#function>

<#-- Forms match full name -->
<#function formFullName matchName teams>
    <#assign fullName = formTeamsPart(teams)/>
    <#list mainEvents as event>
        <#if matchName?contains(event)>
            <#assign fullName = fullName + " - ```" + matchName?upper_case + "```"/>
        </#if>
    </#list>
    <#return fullName/>
</#function>
<#-- Message itself -->
*${leagueName}*

<#if schedule?keys?size == 0>
Split/Season is over.  
No new schedule yet.
<#else>
    <#list schedule?keys as key>
*${key}*
    <#list schedule?values[key_index] as match>
    `${match.getTime().format("HH:mm")} (UTC)` - ${formFullName(match.getName(), match.getTeams())}
    </#list>
    <#-- Adding a blank line untill the last date -->
    <#if key_index < schedule?size - 1>

    </#if>
</#list>
</#if>