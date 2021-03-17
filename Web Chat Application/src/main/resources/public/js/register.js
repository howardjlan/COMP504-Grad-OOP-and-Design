window.onload = function() {
    $("#btn-register").click(function(){validInfo(event)});
}

/**
 * Valid the user's information before creat the account.
 */
function validInfo(event){
    event.preventDefault();
    let username  = $("#input-username").val();
    let pwd = $("#input-pwd").val();
    let firstName = $("#input-firstname").val();
    let lastName = $("#input-lastname").val();
    let age = $("#input-age").val();
    let gender = $("#select-gender option:selected").val();
    let school = $("#input-school").val();
    let department = $("#input-department").val();
    let major = $("#input-major").val();
    let interest = $("#input-interests").val();
    $("#input-username").removeClass("is-invalid");
    $("#username-feedback1").hide();
    $("#username-feedback2").hide();
    $("#input-pwd").removeClass("is-invalid");
    $("#pwd-feedback").hide();
    $("#input-firstname").removeClass("is-invalid");
    $("#firstname-feedback").hide();
    $("#input-lastname").removeClass("is-invalid");
    $("#lastname-feedback").hide();
    $("#input-school").removeClass("is-invalid");
    $("#school-feedback").hide();
    $("#input-interests").removeClass("is-invalid");
    $("#interest-feedback").hide();
    let hasError = false;
    if(!validate(username)) {
       $("#input-username").addClass("is-invalid");
       $("#username-feedback1").show();
       hasError = true;
    }
    if(!validate(pwd)) {
        $("#input-pwd").addClass("is-invalid");
        $("#pwd-feedback").show();
    }
    if(!validate(firstName)) {
        $("#input-firstname").addClass("is-invalid");
        $("#firstname-feedback").show();
        hasError = true;
    }
    if(!validate(lastName)) {
        $("#input-lastname").addClass("is-invalid");
        $("#lastname-feedback").show();
        hasError = true;
    }
    if(!validate(school)) {
        $("#input-school").addClass("is-invalid");
        $("#school-feedback").show();
        hasError = true;
    }
    if(!validate(interest)) {
        $("#input-interests").addClass("is-invalid");
        $("#interest-feedback").show();
        hasError = true;
    }
    if(!hasError) {
        $.post("/register",
            {username:username, pwd:pwd, firstName:firstName, lastName:lastName,
                age:age, gender:gender, school:school, department:department,
                major:major, interests:interest},
            function(data){
            if(data == null) {
                $("#input-username").addClass("is-invalid");
                $("#username-feedback2").show();
            } else {
                $("#reg-form").submit();
            }
        },"json")
    }
}

/**
 * Validate field context
 * @param element
 * @returns {boolean}
 */
const validate=(element)=>{
    return !(element === "" || element == null);
};
