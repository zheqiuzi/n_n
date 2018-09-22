<template>
    <div class="item right" :id="user.id">

        <div class="poker-wrap">
            <p class="text">
                <span class="score">{{user.balance}}</span>
                <span class="name">{{user.nickName}}</span>
            </p>
            <div class="poker">
                <img v-for="card in user.cards" :src="cardSrc.replace('color',card.color).replace('size',card.size)"/>

                <div class="cardType">
                    {{cardType}}
                </div>

            </div>
        </div>

        <div class="info">
            <img class="avatar" :src="(user.avatarUrl==null||user.avatarUrl=='null')?'../avatar.jpg':user.avatarUrl">
        </div>




    </div>

</template>

<script>
    export default {
        data(){
            return {
                cardSrc:"../static/images/poker/poker_size_color.png"
            }
        },
        props:{
            user:{
                type:Object
            }
        },
        computed:{
            cardType:function(){
                if(this.user.gameResult==null)return null;
                if(this.user.gameResult.haveOX==true||this.user.gameResult.haveOX=='true'){
                    var remainder=this.user.gameResult.remainder.toString();
                    remainder.replace("0","牛").replace("9","九").replace("8","八").replace("7","七").replace("6","六").replace("5","五").replace("4","四").replace("3","三").replace("2","二").replace("1","一")
                    return "牛"+remainder;
                }else{
                    return "没牛"
                }

            }
        }
    }
</script>

<style scoped>

</style>