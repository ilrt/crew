/*
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.dibden.facade;

import org.ilrt.dibden.User;
import org.ilrt.dibden.Role;

import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UserManagementFacade.java 89 2008-09-09 14:48:05Z cmmaj $
 *
 **/
public interface UserManagementFacade {

    public boolean isUsernameRegistered(String username);

    public boolean isEmailRegistered(String email);

    public User registerUser(String username, String password, String name, String email);

    public boolean validatePassword(String username, String password);

    public void updatePassword(String username, String password);

    public User getUser(String username);
    
    public void generateNewPassword(String email);

    public List<User> getUsers();

    public List<User> getUsers(int start, int max);

    public int totalUsers();

    public void removeUser(String username);

    public void updateUser(String username, String name, String email);

    public Role createRole(String groupId, String name, String description);

    public List<Role> getRoles();

    public List<Role> getRoles(int start, int max);

    public int totalRoles();

    public void removeRole(String roleId);

    public void addRoleToUser(String username, String roleId);

    public Role getRole(String roleId);

    public void updateRole(String roleId, String name, String description);

    public void removeRoleFromUser(String roleId, String username);

}
