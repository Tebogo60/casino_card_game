// this code creates all the cards used in the game!

const generateDeck = () => {
  for (let i = 0; i < suits.length; i++) {
    for (let j = 1; j <= 10; j++) {
      if (j === 1) {
        arrayOfAllCards.push(`A of ${suits[i]}`);
        arrayOfAllCardsElements.push(
          `<div class="card" ondblclick="doubleClick()" onclick="singleClick()" id="A of ${suits[i]}"><span class="${suitColors[i]}"> A </span><span><img src="svg/suit-${suits[i]}.svg" alt="${suits[i]} img" width="35" /></span></div>`
        );
      } else {
        arrayOfAllCards.push(`${j} of ${suits[i]}`);
        arrayOfAllCardsElements.push(
          `<div class="card" ondblclick="doubleClick()" onclick="singleClick()" id="${j} of ${suits[i]}"><span class="${suitColors[i]}"> ${j} </span><span><img src="svg/suit-${suits[i]}.svg" alt="${suits[i]} img" width="35" /></span></div>`
        );
      }
    }
  }
};
generateDeck();

const shuffle = () => {
  for (let i = 0; i < arrayOfAllCardsElements.length; i++) {
    deckIndexes.push(i);
  }

  for (let j = 0; j < 40; j++) {
    if (deckIndexes.includes(randomIndexes[randomIndexes.length - 1])) {
      deckIndexes.splice(
        deckIndexes.indexOf(randomIndexes[randomIndexes.length - 1]),
        1
      );
    }

    randomIndexes.push(
      deckIndexes[Math.floor(Math.random() * deckIndexes.length)]
    );
  }
  cardAllocator();
};

const deal = () => {
  for (let i = 0; i < 10; i++) {
    player1HoleCardsArray.push(randomIndexes[0]);
    randomIndexes.shift();

    player2HoleCardsArray.push(randomIndexes[0]);
    randomIndexes.shift();
  }

  currentRound++;

  cardAllocator();
};

const cardAllocator = () => {
  deck.innerHTML = "";
  player1HoleCards.innerHTML = "";
  player2HoleCards.innerHTML = "";
  board.innerHTML = "";

  deck.innerHTML = "";
  for (let i = 0; i < randomIndexes.length; i++) {
    deck.innerHTML += arrayOfAllCardsElements[randomIndexes[i]];
  }

  for (let j = 0; j < player1HoleCardsArray.length; j++) {
    player1HoleCards.innerHTML +=
      arrayOfAllCardsElements[player1HoleCardsArray[j]];
  }

  for (let k = 0; k < player2HoleCardsArray.length; k++) {
    player2HoleCards.innerHTML +=
      arrayOfAllCardsElements[player2HoleCardsArray[k]];
  }

  for (let l = 0; l < boardCards.length; l++) {
    board.innerHTML +=
      arrayOfAllCardsElements[arrayOfAllCards.indexOf(boardCards[l])];
  }

  changeRowWidth();
};

deck.addEventListener("click", () => {
  if (currentRound === 0) {
    if (shuffled && !dealed) {
      deal();

      dealed = true;
    }

    if (!shuffled) {
      shuffle();

      shuffled = true;
    }
  }
});

const doubleClick = () => {
  // const clickedCard = event.target;
  // if (board.childNodes.length < 10 && clickedCard.parentElement.id === "row") {
  //   if (
  //     clickedCard.className === "card" &&
  //     clickedCard.parentElement.className === "row"
  //   ) {
  //     activeCard = clickedCard.id;
  //   }
  // }
};

const singleClick = () => {
  if (board.childNodes.length < 10) {
    const clickedCard = event.target;

    if (
      clickedCard.parentElement.parentElement.id === "player1" &&
      player1sTurn
    ) {
      activeCard = clickedCard.id;
      boardCards.push(activeCard);
      player1HoleCardsArray.splice(
        player1HoleCardsArray.indexOf(arrayOfAllCards.indexOf(activeCard)),
        1
      );

      player1sTurn = false;
      console.log("player1sTurn :>> ", player1sTurn);
    }
  }
  cardAllocator();
};
