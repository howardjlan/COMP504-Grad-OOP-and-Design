window.onload = function() {
    $("#btn-login").click(function(){validInfo(event)});
}


/**
 * Valid the user's information before login.
 */
function validInfo(event){
    event.preventDefault();
    let username  = $("#input-username").val();
    let pwd = $("#input-pwd").val();

    $("#input-username").removeClass("is-invalid");
    $("#username-feedback").hide();
    $("#input-pwd").removeClass("is-invalid");
    $("#pwd-feedback1").hide();
    let hasError = false;
    if(!validate(username)) {
        $("#input-username").addClass("is-invalid");
        $("#username-feedback").show();
        hasError = true;
    }
    if(!validate(pwd)) {
        $("#input-pwd").addClass("is-invalid");
        $("#pwd-feedback1").show();
    }
    if(!hasError) {
        $.post("/signin",
            {username:username,pwd:pwd},
            function(data){
                if(data == null) {
                    $("#input-pwd").addClass("is-invalid");
                    $("#pwd-feedback2").show();
                } else {
                    localStorage.setItem("username",username);
                    $("#login-form").submit();
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
    if(element == "" || element == null){
        return false;
    }
    return true;
}
