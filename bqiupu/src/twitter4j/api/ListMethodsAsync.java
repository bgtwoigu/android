/*
Copyright (c) 2007-2010, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package twitter4j.api;

import twitter4j.Paging;
import twitter4j.TwitterListener;

/**
 * @author Joern Huxhorn - jhuxhorn at googlemail.com
 */
public interface ListMethodsAsync {
    /**
     * Creates a new list for the authenticated user. Accounts are limited to 20 lists.
     * <br>This method calls http://api.twitter.com/1/user/lists.json
     *
     * @param listName     The name of the list you are creating. Required.
     * @param isPublicList set true if you wish to make a public list
     * @param description  The description of the list you are creating. Optional.
     * @see <a href="http://dev.twitter.com/doc/post/:user/lists">POST :user/lists | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void createUserList(String listName, boolean isPublicList, String description,TwitterListener listener);

    /**
     * Updates the specified list.
     * <br>This method calls http://api.twitter.com/1/:user/lists/:id.json
     *
     * @param listId         The id of the list to update.
     * @param newListName    What you'd like to change the list's name to.
     * @param isPublicList   Whether your list is public or private. Optional. Values can be public or private. Lists are public by default if no mode is specified.
     * @param newDescription What you'd like to change the list description to.
     * @see <a href="http://dev.twitter.com/doc/post/:user/lists/:id">POST :user/lists/:id | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void updateUserList(int listId, String newListName, boolean isPublicList, String newDescription,TwitterListener listener);

    /**
     * List the lists of the specified user. Private lists will be included if the authenticated users is the same as the user whose lists are being returned.
     * <br>This method calls http://api.twitter.com/1/:user/lists.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param cursor              Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @see <a href="http://dev.twitter.com/doc/get/:user/lists">GET :user/lists | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void getUserLists(String listOwnerScreenName, long cursor,TwitterListener listener);

    /**
     * Show the specified list. Private lists will only be shown if the authenticated user owns the specified list.
     * <br>This method calls http://api.twitter.com/1/:user/lists/:id.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param id                  The id of the list to show
     * @see <a href="http://dev.twitter.com/doc/get/:user/lists/:id">GET :user/lists/:id | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void showUserList(String listOwnerScreenName, int id,TwitterListener listener);

    /**
     * Deletes the specified list. Must be owned by the authenticated user.
     * <br>This method calls http://api.twitter.com/1/:user/lists/:id.json
     *
     * @param listId The id of the list to delete
     * @see <a href="http://dev.twitter.com/doc/delete/:user/lists/:id">DELETE :user/lists/:id | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void destroyUserList(int listId,TwitterListener listener);

    /**
     * Show tweet timeline for members of the specified list.
     * <br>http://api.twitter.com/1/user/lists/list_id/statuses.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param id                  The id of the list to delete
     * @param paging              controls pagination. Supports since_id, max_id, count and page parameters.
     * @see <a href="http://dev.twitter.com/doc/get/:user/lists/:id/statuses">GET :user/lists/:id/statuses | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void getUserListStatuses(String listOwnerScreenName, int id, Paging paging,TwitterListener listener);

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls http://api.twitter.com/1/:user/lists/memberships.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param cursor              Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @see <a href="http://dev.twitter.com/doc/get/:user/lists/memberships">GET :user/lists/memberships | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void getUserListMemberships(String listOwnerScreenName, long cursor,TwitterListener listener);

    /**
     * List the lists the specified user follows.
     * <br>This method calls http://api.twitter.com/1/[user]/lists/subscriptions.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param cursor              Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @see <a href="http://dev.twitter.com/doc/get/:user/lists/subscriptions">GET :user/lists/subscriptions | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
	void getUserListSubscriptions(String listOwnerScreenName, long cursor,TwitterListener listener);
}
