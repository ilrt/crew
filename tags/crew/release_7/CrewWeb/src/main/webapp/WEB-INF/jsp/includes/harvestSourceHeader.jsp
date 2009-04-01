<script type="text/javascript"
        src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<script type="text/javascript">

    // create the input box for a role
    function createRoleInput(role, permission, isChecked) {
        var output = "<input type='checkbox' name='" + role + "' value='" + permission + "'";
        return output += (isChecked) ? " checked='checked'/>" : "/>";
    }

    // remove selected role from the dropdown. if there are no roles left in the list,
    // remove the section from the view
    function removeRoleFromList() {
        $('#otherRoles :selected').remove();

        if ($('#otherRoles > option').size() == 0) {
            $('#newRoleSelection').remove();
        }
    }

    // handle the addRole button
    $(function() {
        $('#addRolesButton').click(function(event) {

            var label = $('#otherRoles :selected').text();
            var authority = "AUTHORITY_" + label;

            $('#permissions tbody').append("<tr><td>" + label + "</td><td>" +
                                           createRoleInput(authority, 1, true) + "</td><td>" +
                                           createRoleInput(authority, 2, false) + "</td><td>" +
                                           createRoleInput(authority, 4, false) + "</td></tr>");

            removeRoleFromList();

            return false;
        });
    });

</script>