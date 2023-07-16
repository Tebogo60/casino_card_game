// // this code creates all the cards that will be used in the game!

// const typesOfCards = ["Hearts", "Diamonds", "Clubs", "Spades"];
// const arrayOfAllCards = [];

// const table = document.querySelector(".table");

// for (let i = 0; i < typesOfCards.length; i++) {
//   for (let j = 1; j <= 10; j++) {
//     if (j === 1) {
//       arrayOfAllCards.push(`Ace of ${typesOfCards[i]}`);
//     } else {
//       arrayOfAllCards.push(`${j} of ${typesOfCards[i]}`);
//     }
//   }
// }

// for (let i = 0; i < arrayOfAllCards.length; i++) {
//   table.innerHTML += "2";
//   //   `<div class="card">
//   //   <span class="color-black"> A </span>
//   //   <span>
//   //     <img src="svg/suit-spade-fill.svg" alt="" width="35" />
//   //   </span>
//   // </div> `;
// }

// // console.log(arrayOfAllCards);
// console.log(arrayOfAllCards.length);

// // this code randomly divides cards between 2 players equally

// const roundCards = arrayOfAllCards.length / 4;
// const player1 = [];
// const player2 = [];

// const randomIndex = () => {
//   const index = Math.floor(Math.random() * arrayOfAllCards.length);

//   return index;
// };

// for (let i = 0; i < roundCards; i++) {
//   player1.push(arrayOfAllCards[randomIndex()]);
//   player2.push(arrayOfAllCards[randomIndex()]);

//   arrayOfAllCards.splice(
//     arrayOfAllCards.indexOf(player1[player1.length - 1]),
//     1
//   );
//   arrayOfAllCards.splice(
//     arrayOfAllCards.indexOf(player2[player2.length - 1]),
//     1
//   );
// }

// console.log(player1);
// console.log(player2);
// console.log(arrayOfAllCards.length);

let arr = [1, 2, 3, 4, 5];

if (arr.includes(1)) {
  arr.splice(arr.indexOf(1), 1);
}

console.log("arr :>> ", arr);
