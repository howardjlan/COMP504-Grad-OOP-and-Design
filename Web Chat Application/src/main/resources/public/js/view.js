'use strict';

//app to draw polymorphic shapes on canvas
var app, gameApp;
let uuid;
let animationFrame = 1;
let bigDotFrame = 0;
let colors = ["R","B","P","O"];
let heart = 3;
let score = 0;
let highestScore = 0;
let eatenDots = 0;
let level = 1;
let keyCode = 0;
let timeDead = 0;
let status = 0;
let interval = 0;
let zoomNotification = 0;


/**
 * Draws the level walls. Static as the game progresses.
 * @param canvas
 * @returns {{dims: {width: *, height: *}, clear: clear, drawWall: drawWall, clearRect: clearRect}}
 */
function createBackgroundApp(canvas) {
    let c = canvas.getContext("2d");

    let drawWall = function(start, end, color) {
        c.strokeStyle = color;
        c.beginPath();
        c.lineWidth = 3;
        // c.moveTo(start[0] * 1.5, start[1] * 1.5);
        // c.lineTo(end[0] * 1.5 ,end[1] * 1.5);
        c.moveTo(start.x, start.y);
        c.lineTo(end.x ,end.y);
        c.stroke();
    };


    let clearRect = function(x, y, width, height) {
        c.clearRect(x, y, width, height);
    }

    let clear = function() {
        c.clearRect(0,0, canvas.width, canvas.height);
    };

    return {
        drawWall,
        clearRect,
        clear,
        dims: {height: canvas.height, width: canvas.width}
    }
}

/**
 * Draws the dots, big dots, sprites, etc. Needs to update each frame.
 * @param canvas
 * @returns {{drawDot: drawDot, dims: {width: *, height: *}, clear: clear, drawSprite: drawSprite, clearRect: clearRect}}
 */
function createGameApp(canvas) {
    let c = canvas.getContext("2d");

    let preRenderCanvas = document.createElement("canvas");
    preRenderCanvas.width = canvas.width;
    preRenderCanvas.height = canvas.height;
    let prc = canvas.getContext("2d");

    let drawSprite = function (image, x, y) {
        prc.drawImage(image, x, y);
    };

    let drawDot = function(x, y, radius) {
        prc.fillStyle = 'yellow';
        prc.beginPath();
        prc.arc(x, y, radius * 2, 0, 2 * Math.PI, false);
        prc.closePath();
        prc.fill();
    };

    let applyRender = function() {
        c.drawImage(preRenderCanvas, 0, 0);
    };

    let clearRect = function(x, y, width, height) {
        prc.clearRect(x, y, width, height);
    };

    let clear = function() {
        prc.clearRect(0,0, canvas.width, canvas.height);
    };

    return {
        drawSprite,
        drawDot,
        applyRender,
        clearRect,
        clear,
        dims: {height: canvas.height, width: canvas.width}
    }
}

$(window).bind('beforeunload',function(){
    clear();
});

/**
 * Entry point into app
 */
window.onload = function() {
    app = createBackgroundApp(document.getElementById("canvas-background"));
    gameApp = createGameApp(document.getElementById("canvas-game-elements"));
    preloadSprites();
    uuid = uuidv4();
    init();
    start();
    $('body').keydown(e => {
        let keyDown = parseInt(e.keyCode ? e.keyCode : e.which) - 36;
        if (keyDown > 0 && keyDown <= 4) {
            keyCode = keyDown;
            if (status === 1 && interval === 0) {
                interval = setInterval(updatePaintObjWorld, 60);
                $("#notification").html("<div style=\"color:forestgreen;\">Game Started!</div>");
                setTimeout(function () {
                    $("#notification").html("<div style=\"color:forestgreen;\">&nbsp;</div>");
                }, 1100);
                $('#btn-pause').prop('disabled', false);
            }
        }
    });

    $("#btn-start").click(start);
    $("#btn-pause").click(pause);
    $("#btn-restart").click(restart);
};

/**
 * Initialize the game level.
 */
function init() {
    switchLevel = false;
    $.get("/init", { uuid: uuid }, function (data) {
        setTimeout(function() {
            data = JSON.parse(data);
            heart = data.maxLives;
            setLivesImg();

            score = data.currentScore;
            setScore();

            animationFrame = 1;
            bigDotFrame = 0;

            setMenu(data.levelCount, data.isZoomAvailable, data.numberOfGhosts, data.maxLives);

            loadWall(data.levelInstance.walls);
            loadDots(data.levelInstance.dots);
            loadBigDots(data.levelInstance.bigDots);
            loadGhosts(data.ghosts);
            loadPacman(data.pacman);
            loadFruits(data.levelInstance.fruits);
            if (data.isZoomAvailable) {
                loadZoom(data.levelInstance.zoom);
            }
        }, 300);
    });
}

/**
 * Helper function for preloading sprites.
 */
function loadImg(src) {
    let img = new Image();
    img.src = src;
    return img;
}

/**
 * Preload images to speed up performance.
 */
function preloadSprites() {
    window.pacmanSprites = [[loadImg("asset/sprites/PacmanL1.png"), loadImg("asset/sprites/PacmanL2.png")],
        [loadImg("asset/sprites/PacmanU1.png"), loadImg("asset/sprites/PacmanU2.png")],
        [loadImg("asset/sprites/PacmanR1.png"), loadImg("asset/sprites/PacmanR2.png")],
        [loadImg("asset/sprites/PacmanD1.png"), loadImg("asset/sprites/PacmanD2.png")]];

    window.deadPacmanSprites = [loadImg("asset/sprites/PacmanClosed.png"),
        loadImg("asset/sprites/DeadPacman1.png"),
        loadImg("asset/sprites/DeadPacman2.png"),
        loadImg("asset/sprites/DeadPacman3.png"),
        loadImg("asset/sprites/DeadPacman4.png"),
        loadImg("asset/sprites/DeadPacman5.png"),
        loadImg("asset/sprites/DeadPacman6.png"),
        loadImg("asset/sprites/DeadPacman7.png"),
        loadImg("asset/sprites/DeadPacman8.png"),
        loadImg("asset/sprites/DeadPacman9.png"),
        loadImg("asset/sprites/DeadPacman10.png"),
        loadImg("asset/sprites/DeadPacman11.png"),
        loadImg("asset/sprites/DeadPacman12.png")];

    window.ghostSprites = [[[loadImg("asset/sprites/RL1.png"), loadImg("asset/sprites/RL2.png")],
        [loadImg("asset/sprites/RU1.png"), loadImg("asset/sprites/RU2.png")],
        [loadImg("asset/sprites/RR1.png"), loadImg("asset/sprites/RR2.png")],
        [loadImg("asset/sprites/RD1.png"), loadImg("asset/sprites/RD2.png")]],
        [[loadImg("asset/sprites/BL1.png"), loadImg("asset/sprites/BL2.png")],
            [loadImg("asset/sprites/BU1.png"), loadImg("asset/sprites/BU2.png")],
            [loadImg("asset/sprites/BR1.png"), loadImg("asset/sprites/BR2.png")],
            [loadImg("asset/sprites/BD1.png"), loadImg("asset/sprites/BD2.png")]],
        [[loadImg("asset/sprites/PL1.png"), loadImg("asset/sprites/PL2.png")],
            [loadImg("asset/sprites/PU1.png"), loadImg("asset/sprites/PU2.png")],
            [loadImg("asset/sprites/PR1.png"), loadImg("asset/sprites/PR2.png")],
            [loadImg("asset/sprites/PD1.png"), loadImg("asset/sprites/PD2.png")]],
        [[loadImg("asset/sprites/OL1.png"), loadImg("asset/sprites/OL2.png")],
            [loadImg("asset/sprites/OU1.png"), loadImg("asset/sprites/OU2.png")],
            [loadImg("asset/sprites/OR1.png"), loadImg("asset/sprites/OR2.png")],
            [loadImg("asset/sprites/OD1.png"), loadImg("asset/sprites/OD2.png")]]];

    window.flashingGhostSprites = [[loadImg("asset/sprites/FlashA1.png"), loadImg("asset/sprites/FlashA2.png")],
        [loadImg("asset/sprites/FlashB1.png"), loadImg("asset/sprites/FlashB2.png")]];

    window.deadGhostSprites = [loadImg("asset/sprites/DeadGhostL.png"),
        loadImg("asset/sprites/DeadGhostU.png"),
        loadImg("asset/sprites/DeadGhostR.png"),
        loadImg("asset/sprites/DeadGhostD.png")];

    window.fruitSprites = [loadImg("asset/sprites/Fruit1.png"),
        loadImg("asset/sprites/Fruit2.png"),
        loadImg("asset/sprites/Fruit3.png"),
        loadImg("asset/sprites/Fruit4.png"),
        loadImg("asset/sprites/Fruit5.png"),
        loadImg("asset/sprites/Fruit6.png"),
        loadImg("asset/sprites/Fruit7.png"),
        loadImg("asset/sprites/Fruit8.png"),
        loadImg("asset/sprites/Fruit9.png")];
}

/**
 * load a wall at a location on the canvas
 */
function loadWall(wall) {
    wall.forEach( function (element) {
        app.drawWall(element.loc, element.endLoc, element.color);
    })
}

/**
 * load dots at a location on the canvas
 */
function loadDots(dots) {
    dots.forEach( function (element) {
        gameApp.drawDot(element.loc.x ,element.loc.y ,element.radius);
    })
}

/**
 * load big dots at a location on the canvas
 */
function loadBigDots(bigDots) {
    if (bigDotFrame < 3) {
        bigDots.forEach( function (element) {
            gameApp.drawDot(element.loc.x, element.loc.y, element.radius);
        })
    } else if (bigDotFrame === 4) {
        bigDotFrame = 0;
    }
    bigDotFrame += 1;
}

/**
 * load pacman at his location on the canvas
 */
function loadPacman(pacman) {
    let loc = [pacman.loc.x - pacman.size, pacman.loc.y - pacman.size];
    let dir = pacman.direction - 1;
    if (pacman.deadState !== -1) {
        gameApp.drawSprite(deadPacmanSprites[pacman.deadState], loc[0], loc[1]);
        //if (timeDead < 12) {
        //    timeDead += 1;
        //}
        //TODO reset timeDead when the board resets on new life restart.
    } else {
        gameApp.drawSprite(pacmanSprites[dir][animationFrame], loc[0], loc[1]);
    }
}

/**
 * load all ghosts at their location on the canvas
 */
function loadGhosts(ghosts) {
    let loc, dir, color;
    ghosts.forEach( function (element) {
        loc = [element.loc.x - element.size, element.loc.y - element.size];
        dir = element.direction - 1;
        color = colors.indexOf(element.color.charAt(0).toUpperCase());
        if (element.isFlashing) {
            let time = Math.floor(element.flashingTimer / 120);
            if (time <= 20 && time % 2 === 0) {
                gameApp.drawSprite(flashingGhostSprites[1][animationFrame], loc[0], loc[1]);
            } else {
                gameApp.drawSprite(flashingGhostSprites[0][animationFrame], loc[0], loc[1]);
            }
        } else if (element.isDead) {
            gameApp.drawSprite(deadGhostSprites[dir], loc[0], loc[1]);
        } else {
            gameApp.drawSprite(ghostSprites[color][dir][animationFrame], loc[0], loc[1]);
        }
    })
}

/**
 * load all fruit at their location on the canvas
 */
function loadFruits(fruits) {
    let loc;
    fruits.forEach( function (element) {
        if (element.timeLeft > 0) {
            loc = [element.loc.x - 14, element.loc.y - 14];
            gameApp.drawSprite(fruitSprites[element.fruitType - 1], loc[0], loc[1]);
        }
    })
}

/**
 * load zoom fruit at its location on the canvas
 */
function loadZoom(zoom) {
    let loc;
    if (zoom !== undefined && zoom !== null) {
        loc = [zoom.loc.x - 14, zoom.loc.y - 14];
        gameApp.drawSprite(fruitSprites[8], loc[0], loc[1]);
    }
}

let switchLevel = false;
let isGameOver = false;
function onGameOver() {
    if (score > highestScore) {
        highestScore = score;
        setHighest();
    }
    if (switchLevel) return;

    switchLevel = true;
    if (level == 1) {
        $("#notification").html("<div style=\"color:white;\">Congratulations! Advanced to level 2!</div>");
        setTimeout(function() {
            let numOfGhosts = $('#select-ghost option:selected').val();
            let numberOfLives= $('#select-lives option:selected').val();
            let isZoomAvailable = $('#zoomSwitch').prop("checked");
            $.post('/setGameParameters', { uuid: uuid, gameLevel: 2, numOfGhosts: numOfGhosts, numberOfLives: numberOfLives,
                isZoomAvailable: isZoomAvailable}, function () {
                restart();
                level = 2;
            }, "json");
        }, 2500);
    }
    else if (level == 2) {
        $("#notification").html("<div style=\"color:white;\">Congratulations! You won the game!</div>");
        isGameOver = true;
    }
}

/**
 *   update the ball and inner walls
 */
function updatePaintObjWorld() {
    if (isGameOver || switchLevel) return;

    if (status === 1) {
        $.post("/update", { uuid: uuid, keyCode: keyCode}, function (data) {
            animationFrame = (animationFrame + 1) % 2;
            clearGame();
            loadDots(data.levelInstance.dots);
            loadBigDots(data.levelInstance.bigDots);
            let dotCount = data.levelInstance.dots.length + data.levelInstance.bigDots.length;
            score = data.currentScore;
            if (dotCount == 0 && !switchLevel) {
                onGameOver();
            }


            loadPacman(data.pacman);
            loadGhosts(data.ghosts);
            loadFruits(data.levelInstance.fruits);
            loadZoom(data.levelInstance.zoom);
            if(data.inEffect && zoomNotification == 0) {
                zoomNotification++;
                $("#notification").html("<div style=\"color:blue;\">ZOOM! ZOOM! ZOOM! Receive 2x score for next 10 seconds!</div>");
                setTimeout(function() {
                    $("#notification").html("<div style=\"color:blue;\">&nbsp;</div>");
                }, 5000);
            }
            eatenDots = data.eatenDots;
            let isLostLife = (heart !== data.pacman.leftLives);
            heart = data.pacman.leftLives;
            setEatenDots();
            setScore();
            setLives();
            gameApp.applyRender();
            if (isLostLife)
            {
                if (heart === 0) {
                    let gameOverString = "Game over!" + " Your score is " + score + "."
                    $("#notification").html("<div style=\"color:#ff0000;\">" + gameOverString + "</div>");
                    $('#btn-pause').prop('disabled', true);
                    endGame();
                    status = 3
                } else if (heart == 1 || heart == 2) {
                    $("#notification").html("<div style=\"color:red;\">Pac-man lost a life.</div>");
                    setTimeout(function() {
                        $("#notification").html("<div style=\"color:#ff0000;\">&nbsp;</div>");
                    }, 1600);
                }
            }
        }, "json");
    }
}

/**
 * Clear the canvas
 */
function clear() {
    $.get("/clear", { uuid: uuid });
    app.clear();
    gameApp.clear();
    timeDead = 0;
    clearInterval(interval);
    interval = 0;
}

/**
 * Clear the game canvas.
 */
function clearGame() {
    gameApp.clear();
}

/**
 * Start the game.
 */
function start() {

    setLivesImg();

    setScore();

    setEatenDots();

    status = 1;
    $("#notification").html("Press any arrow key to start the game.");
}

/**
 * Pause the game.
 */
function pause() {
    if (status !== 2) {
        $("#notification").html("<div style=\"color:red;\">Game Paused</div>");
        status = 2;
        $('#btn-pause').css("background-color", "orange");
        $('#btn-pause').html("Continue");
    }
    else {
        $("#notification").html("<div style=\"color:darkorange;\">Game Resumed</div>");
        setTimeout(function() {
            $("#notification").html("<div style=\"color:forestgreen;\">&nbsp;</div>");
        }, 1100);
        status = 1;
        $('#btn-pause').css("background-color", "red");
        $('#btn-pause').html("Pause");
    }
}

/**
 * Restart the game.
 */
function restart() {
    switchLevel = false;
    isGameOver = false;
    pause()
    setTimeout(function() {
        status = 1;
        $('#btn-pause').css("background-color", "red");
        $('#btn-pause').html("Pause");

        clear();
        init();
        eatenDots = 0;
        setEatenDots();
        $("#notification").html("Press any arrow key to start the game.");
        $('#btn-pause').prop('disabled', true);
    }, 300);
}

/**
 * End the game.
 */
function endGame() {
    if (score > highestScore) {
        highestScore = score;
        setHighest();
    }
    setTimeout(function() {
        restart();
    }, 3000);
}

let heartTemplate = ' <img class="heart" src="asset/heart.png" width="30px" height="30px">';

/**
 * Set lives images.
 */
function setLives() {
    setLivesImg();
}

/**
 * Set lives images.
 */
function setLivesImg() {
    $('#life-anchor').empty();
    $('#life-anchor').append('<div class="mr-3">Life:</div>');
    for (let i = 0; i < heart; i++) {
        $('#life-anchor').append(heartTemplate);
    }
}

/**
 * Set score.
 */
function setScore() {
    $('#score-text').html(score)
}

/**
 * Set eaten dots.
 */
function setEatenDots() {
    $('#dots-text').html(eatenDots)
}

/**
 * Set highest score.
 */
function setHighest() {
    setHighestText();
    $.post("/setHighestScore", { uuid: uuid, score: highestScore}, function () {

    }, "json");
    $('#highest-score-text').val(highestScore)
}

/**
 * Set highest score.
 */
function setHighestText() {
    $('#highest-score-text').val(highestScore)
}

/**
 * Set game setting.
 */
function setGameParameters() {
    let gameLevel = $('#select-level option:selected').val();
    let numOfGhosts = $('#select-ghost option:selected').val();
    let numberOfLives= $('#select-lives option:selected').val();
    let isZoomAvailable = $('#zoomSwitch').prop("checked");

    $.post('/setGameParameters', { uuid: uuid, gameLevel: gameLevel, numOfGhosts: numOfGhosts, numberOfLives: numberOfLives,
        isZoomAvailable: isZoomAvailable}, function () {
        restart();
    }, "json");
}

/**
 * Open game setting.
 */
function openSettings() {
    if (status !== 2) {
        $('#btn-pause').click();
    }
}

/**
 * Close game setting.
 */
function closeSettings() {
    $('#btn-pause').click();
}

/**
 * Set the Menu fields.
 */
function setMenu(gameLevel, hasZoom, numGhost, numLives) {
    $('#select-level').val(gameLevel).prop('selected', true);
    $('#select-ghost').val(numGhost).prop('selected', true);
    $('#select-lives').val(numLives).prop('selected', true);
    $('#zoomSwitch').prop("checked", hasZoom);
}

/**
 * Generate a uuid.
 */
function uuidv4() {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}
