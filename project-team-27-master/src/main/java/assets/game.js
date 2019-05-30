var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
var spaceLaserPopupRemaining = true;
var moveFleetPopupRemaining = true;

var numSunk = 0;
var sonarPulse = false;
var sonarPulseCounter = 2;
var moveFleet = false;
var moveFleetCounter = 2;
var fleetDirection;

var placeShipButtons = document.getElementsByClassName('placeShipButton');

/* Event Handler Function for Clicking on Place Ship Button */
function handlePlaceShipButtonClick(event) {
    for (var i = 0; i < placeShipButtons.length; i++) {
        if (placeShipButtons[i].textContent === 'Placing') {
            placeShipButtons[i].textContent = 'Place';
            placeShipButtons[i].classList.remove('placing-ship-button');
        }
    }
    event.target.textContent = 'Placing';
    event.target.classList.add('placing-ship-button');
}

// Add Click Event Listeners to Each Place Ship Button //
for (var i = 0; i < placeShipButtons.length; i++) {
    placeShipButtons[i].addEventListener('click', handlePlaceShipButtonClick);
}

var sonarPulseCountElem = document.querySelector('.sonar-pulse-count');
sonarPulseCountElem.textContent = sonarPulseCounter;

var sonarPulseButton = document.querySelector('.inactive-sonar-pulse');

/* Event Handler Function for Clicking on Sonar Pulse Button */
function handleSonarPulseButtonClick(event) {
    if (event.target.classList.contains('cancel-sonar-pulse')) {
        sonarPulse = false;
        event.target.classList.remove('cancel-sonar-pulse');
        event.target.textContent = 'Use Sonar Pulse';
    } else {
        sonarPulse = true;
        event.target.classList.add('cancel-sonar-pulse');
        event.target.textContent = 'Cancel Sonar Pulse';
    }
}

var moveFleetButton = document.querySelector('.inactive-move-fleet');

var moveFleetCountElem = document.querySelector('.move-fleet-count');
moveFleetCountElem.textContent = moveFleetCounter;

function handleMoveFleetButtonClick(event) {
    var options = document.getElementById('direction-choices');
    var direction = options.options[options.selectedIndex].value;
    if (direction.length === 1) {
        sendXhr(
            "POST",
            "/move", {
                game: game,
                direction: direction
            },
            function(data) {
                game = data;
                redrawGrid();

                // Decrement Move Fleet Counter then Reset Move Fleet Flag //
                if (moveFleet) {
                    moveFleetCounter--;
                    moveFleetCountElem.textContent = moveFleetCounter;
                    moveFleet = false;
                }

                // Remove Move Fleet Button Functionality  //
                if (moveFleetCounter === 0) {
                    moveFleetButton.removeEventListener('click', handleMoveFleetButtonClick);
                    moveFleetButton.classList.remove('active-move-fleet');
                    moveFleetButton.classList.add('inactive-move-fleet');
                }
            }
        );
    }
}

function makeGrid(table, isPlayer) {
    for (i = 0; i < 10; i++) {
        let row = document.createElement("tr");
        for (j = 0; j < 10; j++) {
            let column = document.createElement("td");
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function getNumSunk(game) {
    numSunk = 0;
    game.opponentsBoard.ships.forEach(ship => {
        if (ship.occupiedSquares[ship.locationOfCapQuart].hit) {
            numSunk++;
        }
    });

    return numSunk;
}

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach(attack => {
        let className;
        if (attack.result === "SUNK") {
            className = "sink";

            // Find Freshly Sunken Ship to Update It //
            var sunkShip;
            var sunkenShipFound = false;
            for (let i = 0; i < board.ships.length; i++) {
                // Find Matching Square //
                for (let j = 0; j < board.ships[i].occupiedSquares.length; j++) {
                    if (attack.location.row === board.ships[i].occupiedSquares[j].row && attack.location.column === board.ships[i].occupiedSquares[j].column) {
                        sunkenShipFound = true;
                        break;
                    }
                }

                // Store Found Ship //
                if (sunkenShipFound) {
                    sunkShip = board.ships[i];
                    break;
                }
            }

            // Change All of Ship's Hit Squares to Sunk //
            var alreadyAttacked;
            sunkShip.occupiedSquares.forEach(square => {
                alreadyAttacked = false;
                board.attacks.forEach(currAttack => {
                    if (currAttack.location.row === square.row && currAttack.location.column === square.column) {
                        // Update Attacked Square //
                        alreadyAttacked = true;
                        currAttack.result = "SUNK";

                        // Apply sink Class //
                        document
                            .getElementById(elementId)
                            .rows[currAttack.location.row - 1].cells[
                                currAttack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                            ].classList.add(className);
                    }
                });

                // Find Remaining Squares to Set to Sunk //
                if (!alreadyAttacked) {
                    // Add Object to board's attacks Array //
                    board.attacks.unshift({
                        location: {
                            row: square.row,
                            column: square.column,
                            hit: square.hit
                        },
                        result: "SUNK",
                        ship: null
                    });

                    // Apply sink Class //
                    document
                        .getElementById(elementId)
                        .rows[square.row - 1].cells[
                            square.column.charCodeAt(0) - "A".charCodeAt(0)
                        ].classList.add(className);
                }
            });

            numSunk = getNumSunk(game);

            // If Player Has Sunk at least 1 Ship, Activate Sonar Pulse Button //
            if (numSunk >= 1 && sonarPulseCounter > 0) {
                sonarPulseButton.classList.add('active-sonar-pulse');
                sonarPulseButton.classList.remove('inactive-sonar-pulse');
                sonarPulseButton.addEventListener('click', handleSonarPulseButtonClick);
            }

            // If Player Has Sunk at least 1 Ship, Display Space Laser Popup //
            if (numSunk >= 1 && spaceLaserPopupRemaining) {
                spaceLaserPopupRemaining = false;
                swal({
                    title: 'Upgrade',
                    text: 'Captain, you\'ve unlocked the space laser! Now you can hit submarines!',
                    icon: 'success',
                    button: 'Arrggghhh'
                });
            }

            // If Player Has Sunk at least 2 ships, Activate Move Fleet Button //
            if (numSunk >= 2 && moveFleetCounter > 0) {
                moveFleetButton.classList.add('active-move-fleet');
                moveFleetButton.classList.remove('inactive-move-fleet');
                moveFleetButton.addEventListener('click', handleMoveFleetButtonClick);
            }
        } else if (attack.result === "MISS") className = "miss";
        else if (attack.result === "HIT") className = "hit";
        else if (attack.result === "SURRENDER") {
            // alert(surrenderText);
            // Register Attacked Square //
            className = "sink";
            document
                .getElementById(elementId)
                .rows[attack.location.row - 1].cells[
                    attack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                ].classList.add(className);

            // Find Freshly Sunken Ship to Update It //
            var sunkShip;
            var sunkenShipFound = false;
            for (let i = 0; i < board.ships.length; i++) {
                // Find Matching Square //
                for (let j = 0; j < board.ships[i].occupiedSquares.length; j++) {
                    if (attack.location.row === board.ships[i].occupiedSquares[j].row && attack.location.column === board.ships[i].occupiedSquares[j].column) {
                        sunkenShipFound = true;
                        break;
                    }
                }

                // Store Found Ship //
                if (sunkenShipFound) {
                    sunkShip = board.ships[i];
                    break;
                }
            }

            // Change All of Ship's Hit Squares to Sunk //
            var alreadyAttacked;
            sunkShip.occupiedSquares.forEach(square => {
                alreadyAttacked = false;
                board.attacks.forEach(currAttack => {
                    if (currAttack.location.row === square.row && currAttack.location.column === square.column) {
                        // Update Attacked Square //
                        alreadyAttacked = true;
                        currAttack.result = "SUNK";

                        // Apply sink Class //
                        document
                            .getElementById(elementId)
                            .rows[currAttack.location.row - 1].cells[
                                currAttack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                            ].classList.add(className);

                        if (document.getElementById(elementId).rows[currAttack.location.row - 1].cells[currAttack.location.column.charCodeAt(0) - "A".charCodeAt(0)].classList.contains('sonar-occupied')) {
                            document
                                .getElementById(elementId)
                                .rows[currAttack.location.row - 1].cells[
                                    currAttack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                                ].classList.remove('sonar-occupied');
                        }
                    }
                });

                // Find Remaining Squares to Set to Sunk //
                if (!alreadyAttacked) {
                    // Add Object to board's attacks Array //
                    board.attacks.unshift({
                        location: {
                            row: square.row,
                            column: square.column,
                            hit: square.hit
                        },
                        result: "SUNK",
                        ship: null
                    });

                    // Apply sink Class //
                    document
                        .getElementById(elementId)
                        .rows[square.row - 1].cells[
                            square.column.charCodeAt(0) - "A".charCodeAt(0)
                        ].classList.add(className);
                }
            });

            if (elementId === "opponent") {
                swal({
                    title: "Game Over",
                    text: surrenderText,
                    icon: "success",
                    button: "Later Gator"
                });

            } else {
                swal({
                    title: "Game Over",
                    text: surrenderText,
                    icon: "error",
                    button: "Later Gator"
                });
            }
        }

        // Apply Color to Square //
        document
            .getElementById(elementId)
            .rows[attack.location.row - 1].cells[
                attack.location.column.charCodeAt(0) - "A".charCodeAt(0)
            ].classList.add(className);
        if (document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - "A".charCodeAt(0)].classList.contains('sonar-free')) {
            document
                .getElementById(elementId)
                .rows[attack.location.row - 1].cells[
                    attack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                ].classList.remove('sonar-free');
        }

        if (document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - "A".charCodeAt(0)].classList.contains('sonar-occupied')) {
            document.getElementById(elementId)
                .rows[attack.location.row - 1].cells[
                    attack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                ].classList.remove('sonar-occupied');
        }

        if (document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - "A".charCodeAt(0)].classList.contains('miss')) {
            if (className === 'hit' || className === 'sink') {
                document
                    .getElementById(elementId)
                    .rows[attack.location.row - 1].cells[
                        attack.location.column.charCodeAt(0) - "A".charCodeAt(0)
                    ].classList.remove('miss');
            }
        }
    });
}

function markSonarPulses(game) {
    game.sonarPulses1.forEach(square => {
        var className;
        if (square.result === 'FREE') className = 'sonar-free';
        else if (square.result === 'OCCUPIED') className = 'sonar-occupied';
        document
            .getElementById('opponent')
            .rows[square.location.row - 1].cells[
                square.location.column.charCodeAt(0) - "A".charCodeAt(0)
            ].classList.add(className);
    });
    game.sonarPulses2.forEach(square => {
        var className;
        if (square.result === 'FREE') className = 'sonar-free';
        else if (square.result === 'OCCUPIED') className = 'sonar-occupied';
        document
            .getElementById('opponent')
            .rows[square.location.row - 1].cells[
                square.location.column.charCodeAt(0) - "A".charCodeAt(0)
            ].classList.add(className);
    });

    // Remove Colors that Cover Recently Sunken Squares //
    game.opponentsBoard.attacks.forEach(attack => {
        let currSquare = document.getElementById('opponent').rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)];
        if ((attack.result === 'MISS' || attack.result === 'HIT' || attack.result === 'SUNK') && currSquare.classList.contains('sonar-occupied')) {
            currSquare.classList.remove('sonar-occupied');
        }
        if ((attack.result === 'MISS' || attack.result === 'HIT' || attack.result === 'SUNK') && currSquare.classList.contains('sonar-free')) {
            currSquare.classList.remove('sonar-free');
        }
    });
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach(row =>
        row.remove()
    );
    Array.from(document.getElementById("player").childNodes).forEach(row =>
        row.remove()
    );
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach(ship =>
        ship.occupiedSquares.forEach(square => {
            document
                .getElementById("player")
                .rows[square.row - 1].cells[
                    square.column.charCodeAt(0) - "A".charCodeAt(0)
                ].classList.add("occupied");
        })
    );
    markHits(
        game.opponentsBoard,
        "opponent",
        "Congratulations, captain! We sunk all enemy ships!"
    );
    markHits(
        game.playersBoard,
        "player",
        "Captain! All of our ships are sunk!"
    );
    markSonarPulses(game);
}

var oldListener;

function registerCellListener(f) {
    let el = document.getElementById("player");
    for (i = 0; i < 10; i++) {
        for (j = 0; j < 10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}

var badPlacement = false;

function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    // If Still Setting Up Player Ships //
    if (isSetup) {
        sendXhr(
            "POST",
            "/place", {
                game: game,
                shipType: shipType,
                x: row,
                y: col,
                isVertical: vertical
            },
            function(data) {
                game = data;
                redrawGrid();
                placedShips++;

                // Grab Respective Ship //
                var activePlaceShipButton;
                if (shipType === "MINESWEEPER") {
                    activePlaceShipButton = document.getElementById('place_minesweeper');
                } else if (shipType === "DESTROYER") {
                    activePlaceShipButton = document.getElementById('place_destroyer');
                } else if (shipType === "BATTLESHIP") {
                    activePlaceShipButton = document.getElementById('place_battleship');
                } else if (shipType === "SUBMARINE") {
                    activePlaceShipButton = document.getElementById('place_submarine');
                }

                // Handle Overlapping Ship Placement //
                var currShips = game.playersBoard.ships;
                var placedShipFound = false;
                for (let i = 0; i < currShips.length; i++) {
                    if (currShips[i].kind === shipType) {
                        placedShipFound = true;
                        break;
                    }
                }

                if (!placedShipFound) {
                    badPlacement = true;
                }

                // Handle Valid Placed Ship //
                if (!badPlacement) {
                    console.log(game);

                    // Remove Place Ship Functionality from Respective Button if Valid Placement //
                    if (activePlaceShipButton.classList.contains('placing-ship-button')) {
                        activePlaceShipButton.removeEventListener('click', handlePlaceShipButtonClick);
                        activePlaceShipButton.classList.remove('placeShipButton');
                        activePlaceShipButton.classList.remove('placing-ship-button');
                        activePlaceShipButton.classList.add('placed-ship-button');
                        activePlaceShipButton.textContent = 'Placed';
                    }
                } else if (activePlaceShipButton.classList.contains('placing-ship-button')) {
                    // Change Button Text Content Back to "Place" if Invalid Placement //
                    activePlaceShipButton.textContent = 'Place';
                }

                if (placedShips === 4) {
                    isSetup = false;
                    registerCellListener(e => {});
                }
            }
        );
    } else {
        sendXhr(
            "POST",
            "/attack", {
                game: game,
                x: row,
                y: col,
                isPulsey: sonarPulse
            },
            function(data) {
                game = data;
                redrawGrid();

                console.log(game);

                // Return Sonar Pulse Button from Cancel to Use State After Usage //
                if (sonarPulseButton.classList.contains('cancel-sonar-pulse')) {
                    sonarPulseButton.classList.remove('cancel-sonar-pulse');
                    sonarPulseButton.classList.add('active-sonar-pulse');
                    sonarPulseButton.textContent = 'Use Sonar Pulse';
                    sonarPulseCountElem.textContent = sonarPulseCounter;
                }

                // Decrement Sonar Pulse Counter then Reset Sonar Pulse Flag //
                if (sonarPulse) {
                    sonarPulseCounter--;
                    sonarPulseCountElem.textContent = sonarPulseCounter;
                    sonarPulse = false;
                }

                // Remove Sonar Pulse Button Functionality  //
                if (sonarPulseCounter === 0) {
                    sonarPulseButton.removeEventListener('click', handleSonarPulseButtonClick);
                    sonarPulseButton.classList.remove('active-sonar-pulse');
                    if (sonarPulseButton.classList.contains('cancel-sonar-pulse')) {
                        sonarPulseButton.classList.remove('cancel-sonar-pulse');
                    }
                    sonarPulseButton.classList.add('inactive-sonar-pulse');
                }
            }
        );
    }
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            // alert("Captain! We can't attack/place our ship here!");
            swal({
                title: "Invalid Placement",
                text: "Captain! We can't attack/place our ship here!",
                icon: "error",
                button: "Sry mateys"
            });
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = document.getElementById("is_vertical").checked;
        let table = document.getElementById("player");
        let cell;
        badPlacement = false;
        for (let i = 0; i < size; i++) {
            if (vertical) {
                let tableRow = table.rows[row + i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    badPlacement = true;
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col + i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                badPlacement = true;
                break;
            }
            cell.classList.toggle("placed");
        }
        if (shipType === "SUBMARINE") {
            if (vertical) {
                if ((row + 2) >= 0 && (row + 2) <= 9 && (col + 1) >= 0 && (col + 1) <= 9) {
                    cell = table.rows[row + 2].cells[col + 1];
                }
            } else {
                if ((row - 1) >= 0 && (row - 1) <= 9 && (col + 2) >= 0 && (col + 2) <= 9) {
                    cell = table.rows[row - 1].cells[col + 2];
                }
            }
            cell.classList.toggle('placed');
        }
    };
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    document
        .getElementById("place_minesweeper")
        .addEventListener("click", function(e) {
            shipType = "MINESWEEPER";
            registerCellListener(place(2));
        });
    document
        .getElementById("place_destroyer")
        .addEventListener("click", function(e) {
            shipType = "DESTROYER";
            registerCellListener(place(3));
        });
    document
        .getElementById("place_battleship")
        .addEventListener("click", function(e) {
            shipType = "BATTLESHIP";
            registerCellListener(place(4));
        });
    document
        .getElementById('place_submarine')
        .addEventListener('click', function(e) {
            shipType = 'SUBMARINE';
            registerCellListener(place(4));
            // registerCellListener(placeBlip());
        })
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
}

var battlelog = document.getElementById("battlelog");
var typewriter = new Typewriter(battlelog, {
    loop: false,
    delay: 28,
    devmode: true
});

typewriter
    .typeString('Welcome to Battleship, captain! ')
    .pauseFor(1500)
    .typeString('<br />')
    .typeString('I am your consol battlelog, here to help! ')
    .pauseFor(1500)
    .typeString('<br />')
    .typeString('Your objective is to kill everybody #topkek.')
    .pauseFor(750)
    .deleteChars(9)
    .typeString('! ')
    .pauseFor(550)
    .typeString('<br />')
    .typeString('Begin by picking ships to the right and clicking on the board on the right side to place them. Submarines can sit on the surface or hide underwater. ')
    .pauseFor(1500)
    .typeString('<br />')
    .pauseFor(550)
    .typeString('Then, after all three ships are placed, click the board to the left to place attacks. ')
    .typeString('<br />')
    .pauseFor(550)
    .typeString('Once you have sunken a ship, you will be able to attack with a space laser and place sonar pulses, which will allow you to see enemy ships. Be careful! You can only use this twice. ')
    .typeString('<br />')
    .pauseFor(550)
    .typeString('Finally, after sinking two enemy ships, you can secretly move your fleet to outsmart your opponent. ')
    .typeString('<br />')
    .start();

// Function bascially allows for the window to auto scroll to the bottom. Used for the battle log
function updateScrollLog() {
    battlelog.scrollTop = battlelog.scrollHeight;
}