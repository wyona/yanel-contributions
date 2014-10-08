/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.wyona.yanel.impl.resources.github;

import org.wyona.yanel.htmlunit.AbstractHtmlUnitTest;

/**
 * Please add a test to ensure your functionality.
 * how to write tests see: http://yanel.wyona.org/en/documentation/testing-framework.html
 */
public class PostReceiveResourceWebTest extends AbstractHtmlUnitTest {

    protected void setUp() throws Exception {
        this.testName = "PostReceiveResource Web Test";
        super.setUp();
    }

    /**
     * Requests post receive URL and verifies content.
     */
    public void testPostReceiveResource() throws Exception {
        com.gargoylesoftware.htmlunit.WebRequest webRequest = new com.gargoylesoftware.htmlunit.WebRequest(new java.net.URL("http://127.0.0.1:8080/yanel/wyona-com/scm-postreceive"));
        //webRequest.setAdditionalHeader("content-type", "application/json");
        webRequest.setAdditionalHeader("content-type", "application/x-www-form-urlencoded");
        webRequest.setHttpMethod(com.gargoylesoftware.htmlunit.HttpMethod.POST);
        java.util.ArrayList parameters = new java.util.ArrayList();
        parameters.add(new com.gargoylesoftware.htmlunit.util.NameValuePair("payload", getEncodedJson()));
        webRequest.setRequestParameters(parameters);
        com.gargoylesoftware.htmlunit.WebClient webClient = new com.gargoylesoftware.htmlunit.WebClient();
        response = webClient.getPage(webRequest).getWebResponse();
        String contentType = response.getContentType();
        assertTrue("Returned content type should be text/plain", contentType.equals("text/plain"));
        assertResourceContainsText("post-receive");

/*
        loadResource("wyona-com/scm-postreceive"); // IMPORTANT: Please make sure that the realm is configured with the prefix 'wyona-com'!
        int statusCode = response.getStatusCode();
        assertTrue("Returned status code should be 501", statusCode == 501);
*/
    }

    /**
     * Get encoded json sample
     */
    private String getEncodedJson() {
        return "%7B%22ref%22%3A%22refs%2Fheads%2Fcontinuous-deployment%22%2C%22before%22%3A%22b139ba345ce5e7189bba2f9f4128883bcd48426a%22%2C%22after%22%3A%22ad136cd72ad6eb91d750ce1babf095abfcf8301e%22%2C%22created%22%3Afalse%2C%22deleted%22%3Afalse%2C%22forced%22%3Afalse%2C%22base_ref%22%3Anull%2C%22compare%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%2Fcompare%2Fb139ba345ce5...ad136cd72ad6%22%2C%22commits%22%3A%5B%7B%22id%22%3A%22ad136cd72ad6eb91d750ce1babf095abfcf8301e%22%2C%22distinct%22%3Atrue%2C%22message%22%3A%22servers+tag+added%22%2C%22timestamp%22%3A%222014-10-07T11%3A09%3A54%2B02%3A00%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%2Fcommit%2Fad136cd72ad6eb91d750ce1babf095abfcf8301e%22%2C%22author%22%3A%7B%22name%22%3A%22Michael+Wechner%22%2C%22email%22%3A%22michael.wechner%40wyona.com%22%7D%2C%22committer%22%3A%7B%22name%22%3A%22Michael+Wechner%22%2C%22email%22%3A%22michael.wechner%40wyona.com%22%7D%2C%22added%22%3A%5B%5D%2C%22removed%22%3A%5B%5D%2C%22modified%22%3A%5B%22continuous-deployment%2Fservers.xml%22%5D%7D%5D%2C%22head_commit%22%3A%7B%22id%22%3A%22ad136cd72ad6eb91d750ce1babf095abfcf8301e%22%2C%22distinct%22%3Atrue%2C%22message%22%3A%22servers+tag+added%22%2C%22timestamp%22%3A%222014-10-07T11%3A09%3A54%2B02%3A00%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%2Fcommit%2Fad136cd72ad6eb91d750ce1babf095abfcf8301e%22%2C%22author%22%3A%7B%22name%22%3A%22Michael+Wechner%22%2C%22email%22%3A%22michael.wechner%40wyona.com%22%7D%2C%22committer%22%3A%7B%22name%22%3A%22Michael+Wechner%22%2C%22email%22%3A%22michael.wechner%40wyona.com%22%7D%2C%22added%22%3A%5B%5D%2C%22removed%22%3A%5B%5D%2C%22modified%22%3A%5B%22continuous-deployment%2Fservers.xml%22%5D%7D%2C%22repository%22%3A%7B%22id%22%3A2459552%2C%22name%22%3A%22yanel%22%2C%22full_name%22%3A%22wyona%2Fyanel%22%2C%22owner%22%3A%7B%22name%22%3A%22wyona%22%2C%22email%22%3Anull%7D%2C%22private%22%3Afalse%2C%22html_url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%22%2C%22description%22%3A%22%22%2C%22fork%22%3Afalse%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%22%2C%22forks_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fforks%22%2C%22keys_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fkeys%7B%2Fkey_id%7D%22%2C%22collaborators_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcollaborators%7B%2Fcollaborator%7D%22%2C%22teams_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fteams%22%2C%22hooks_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fhooks%22%2C%22issue_events_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fissues%2Fevents%7B%2Fnumber%7D%22%2C%22events_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fevents%22%2C%22assignees_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fassignees%7B%2Fuser%7D%22%2C%22branches_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fbranches%7B%2Fbranch%7D%22%2C%22tags_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Ftags%22%2C%22blobs_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fgit%2Fblobs%7B%2Fsha%7D%22%2C%22git_tags_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fgit%2Ftags%7B%2Fsha%7D%22%2C%22git_refs_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fgit%2Frefs%7B%2Fsha%7D%22%2C%22trees_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fgit%2Ftrees%7B%2Fsha%7D%22%2C%22statuses_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fstatuses%2F%7Bsha%7D%22%2C%22languages_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Flanguages%22%2C%22stargazers_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fstargazers%22%2C%22contributors_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcontributors%22%2C%22subscribers_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fsubscribers%22%2C%22subscription_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fsubscription%22%2C%22commits_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcommits%7B%2Fsha%7D%22%2C%22git_commits_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fgit%2Fcommits%7B%2Fsha%7D%22%2C%22comments_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcomments%7B%2Fnumber%7D%22%2C%22issue_comment_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fissues%2Fcomments%2F%7Bnumber%7D%22%2C%22contents_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcontents%2F%7B%2Bpath%7D%22%2C%22compare_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fcompare%2F%7Bbase%7D...%7Bhead%7D%22%2C%22merges_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fmerges%22%2C%22archive_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2F%7Barchive_format%7D%7B%2Fref%7D%22%2C%22downloads_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fdownloads%22%2C%22issues_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fissues%7B%2Fnumber%7D%22%2C%22pulls_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fpulls%7B%2Fnumber%7D%22%2C%22milestones_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fmilestones%7B%2Fnumber%7D%22%2C%22notifications_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Fnotifications%7B%3Fsince%2Call%2Cparticipating%7D%22%2C%22labels_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Flabels%7B%2Fname%7D%22%2C%22releases_url%22%3A%22https%3A%2F%2Fapi.github.com%2Frepos%2Fwyona%2Fyanel%2Freleases%7B%2Fid%7D%22%2C%22created_at%22%3A1317032778%2C%22updated_at%22%3A%222014-09-12T07%3A55%3A20Z%22%2C%22pushed_at%22%3A1412673011%2C%22git_url%22%3A%22git%3A%2F%2Fgithub.com%2Fwyona%2Fyanel.git%22%2C%22ssh_url%22%3A%22git%40github.com%3Awyona%2Fyanel.git%22%2C%22clone_url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel.git%22%2C%22svn_url%22%3A%22https%3A%2F%2Fgithub.com%2Fwyona%2Fyanel%22%2C%22homepage%22%3A%22http%3A%2F%2Fwww.yanel.org%22%2C%22size%22%3A49388%2C%22stargazers_count%22%3A7%2C%22watchers_count%22%3A7%2C%22language%22%3A%22Java%22%2C%22has_issues%22%3Atrue%2C%22has_downloads%22%3Atrue%2C%22has_wiki%22%3Atrue%2C%22has_pages%22%3Afalse%2C%22forks_count%22%3A2%2C%22mirror_url%22%3Anull%2C%22open_issues_count%22%3A43%2C%22forks%22%3A2%2C%22open_issues%22%3A43%2C%22watchers%22%3A7%2C%22default_branch%22%3A%22master%22%2C%22stargazers%22%3A7%2C%22master_branch%22%3A%22master%22%2C%22organization%22%3A%22wyona%22%7D%2C%22pusher%22%3A%7B%22name%22%3A%22michaelwechner%22%2C%22email%22%3A%22michi%40wyona.com%22%7D%2C%22organization%22%3A%7B%22login%22%3A%22wyona%22%2C%22id%22%3A1074625%2C%22url%22%3A%22https%3A%2F%2Fapi.github.com%2Forgs%2Fwyona%22%2C%22repos_url%22%3A%22https%3A%2F%2Fapi.github.com%2Forgs%2Fwyona%2Frepos%22%2C%22events_url%22%3A%22https%3A%2F%2Fapi.github.com%2Forgs%2Fwyona%2Fevents%22%2C%22members_url%22%3A%22https%3A%2F%2Fapi.github.com%2Forgs%2Fwyona%2Fmembers%7B%2Fmember%7D%22%2C%22public_members_url%22%3A%22https%3A%2F%2Fapi.github.com%2Forgs%2Fwyona%2Fpublic_members%7B%2Fmember%7D%22%2C%22avatar_url%22%3A%22https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F1074625%3Fv%3D2%22%7D%2C%22sender%22%3A%7B%22login%22%3A%22michaelwechner%22%2C%22id%22%3A1078960%2C%22avatar_url%22%3A%22https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F1078960%3Fv%3D2%22%2C%22gravatar_id%22%3A%22%22%2C%22url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%22%2C%22html_url%22%3A%22https%3A%2F%2Fgithub.com%2Fmichaelwechner%22%2C%22followers_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Ffollowers%22%2C%22following_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Ffollowing%7B%2Fother_user%7D%22%2C%22gists_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Fgists%7B%2Fgist_id%7D%22%2C%22starred_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Fstarred%7B%2Fowner%7D%7B%2Frepo%7D%22%2C%22subscriptions_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Fsubscriptions%22%2C%22organizations_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Forgs%22%2C%22repos_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Frepos%22%2C%22events_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Fevents%7B%2Fprivacy%7D%22%2C%22received_events_url%22%3A%22https%3A%2F%2Fapi.github.com%2Fusers%2Fmichaelwechner%2Freceived_events%22%2C%22type%22%3A%22User%22%2C%22site_admin%22%3Afalse%7D%7D";
    }
}
