<template>
    <div class="item my">

        <div class="info">
            <img class="avatar" :src="(my.avatarUrl==null||my.avatarUrl=='null')?'../avatar.jpg':my.avatarUrl">
        </div>
        <div class="poker-wrap">
            <p class="text">
                <span class="name">{{my.nickName}}</span>
                <span class="score">{{my.balance}}</span>
            </p>
            <div class="poker">
                <img v-for="card in my.cards" :src="cardSrc.replace('color',card.color).replace('size',card.size)"/>
            </div>
        </div>
    </div>

</template>

<script>
    export default {
        name: "my",
        props:{
            my:Object
        },
        data(){
            return {
                cardSrc:"../static/images/poker/poker_size_color.png"
            }
        },
    }
</script>

<style scoped>
    .my{
        display: flex;
        flex-direction: row;
        flex-wrap: nowrap;
        align-items: center;
        width: 100%;
    }
    .my .poker{
        height: 75px;
    }
    .my .poker img{
        width :42px;
        height: 59px;;
        position: relative;
        float: left;
        margin: 5px;
    }

</style>