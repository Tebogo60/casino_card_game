// this code creates all the cards used in the game!
const gameCards = () => {
  for (let i = 0; i < suits.length; i++) {
    for (let j = 1; j <= 10; j++) {
      if (j === 1) {
        allCardsId.push(`A of ${suits[i]}`);
        tempAllCards.push(
          `<div onclick="cardOnClick()" class="s_card ${suitsColors[i]}" id="A of ${suits[i]}"><span>A</span>${suitSvg[i]}</div>`
        );
        deckOfCards.innerHTML += `<div class="s_card ${suitsColors[i]}" id="A of ${suits[i]}"><span>A</span>${suitSvg[i]}</div>`;
      } else {
        allCardsId.push(`${j} of ${suits[i]}`);
        tempAllCards.push(
          `<div onclick="cardOnClick()" class="s_card ${suitsColors[i]}" id="${j} of ${suits[i]}"><span>${j}</span>${suitSvg[i]}</div>`
        );
        deckOfCards.innerHTML += `<div class="s_card ${suitsColors[i]}" id="${j} of ${suits[i]}"><span>${j}</span>${suitSvg[i]}</div>`;
      }
    }
  }
};
gameCards();

for (let i = 0; i < deckOfCardsChildren.length; i++) {
  deckOfCardsChildren[i].style.top = `calc(50% - ${i / 2}px`;
}

const randomIndex = () => {
  let index = Math.floor(Math.random() * cardIndex.length);
  return index;
};

const shuffle = () => {
  let randomIndexArr = [];

  for (let i = 0; i < deckOfCardsChildren.length; i++) {
    cardIndex.push(i);
  }

  for (let j = 0; j < 40; j++) {
    randomIndexArr.push(cardIndex[randomIndex()]);

    cardIndex.splice(
      cardIndex.indexOf(randomIndexArr[randomIndexArr.length - 1]),
      1
    );
  }

  for (let k = 0; k < randomIndexArr.length; k++) {
    allCards.push(tempAllCards[randomIndexArr[k]]);
  }
};
shuffle();

let i = 0;
let j = 0;

const deal = () => {
  if (i < 20) {
    if (i % 2 !== 0) {
      deckOfCardsChildren[i].classList.add("card-top");
      deckOfCardsChildren[i].style.top = `50%`;

      botHoleCards.push(allCards[i]);
      topCards.innerHTML += botHoleCards[botHoleCards.length - 1];
    } else {
      deckOfCardsChildren[i].classList.add("card-bottom");
      deckOfCardsChildren[i].style.top = `50%`;

      player1HoleCards.push(allCards[i]);
      bottomCards.innerHTML += player1HoleCards[player1HoleCards.length - 1];
    }

    deckOfCardsChildren[i].style.opacity = "0";

    setTimeout(deal, 75);
  }

  i++;

  changeWidth();
};
