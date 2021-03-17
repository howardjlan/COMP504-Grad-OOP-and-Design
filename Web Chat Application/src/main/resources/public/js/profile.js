
let template = '<li class="list-group-item">NAME-VARIANT</li>';

window.onload = function() {
    if(window.localStorage){
        username = localStorage.getItem("username");
    }
    if(username !== undefined || username !== ""){
        $("#span-username").empty();
        $("#span-username").append(document.createTextNode(username));
        loadProfile();
    }

    $("#link-signOut").click(function(){signOut(event)});
};

/**
 * Adds interests.
 * @param data interests
 */
function addInterest(data) {
    let interest = template;
    interest = interest.replace('NAME-VARIANT', data);
    $('#interest-list').append(interest);
}

/**
 * Signs a user out.
 * @param event input event
 */
function signOut(event) {
    event.preventDefault();
    localStorage.removeItem("username");
    location.href = "index.html";
}

/**
 * Load user profile.
 */
function loadProfile() {
    $.post("/profile", {username: username}, function (data) {
        if(data != null) {
            $("#profile-name").append(document.createTextNode(data.username));
            $("#username").val(data.username);
            let firstname = data.name.split(" ")[0];
            let lastname = data.name.split(" ")[1];
            $("#firstName").val(firstname);
            $("#lastName").val(lastname);
            $("#age").val(data.age);
            let gender = data.gender;
            $("#gender option[value=" + gender + "]").prop("selected",true);
            $("#university").val(data.school);
            $("#major").val(data.major);
            $("#department").val(data.department);
            for (let interest of data.interests) {
                addInterest(interest);
            }
        }
    }, "json");

}
