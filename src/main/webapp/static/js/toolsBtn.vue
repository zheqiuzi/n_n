<template>
    <div class="btns">
        <div v-if="timer.timerType=='settingWager'">
            <button data-multiple="1" @click="setwager(1)" data-selector="setWager">1倍</button>
            <button data-multiple="2" @click="setwager(2)" data-selector="setWager">2倍</button>
            <button data-multiple="4" @click="setwager(4)" data-selector="setWager">4倍</button>
        </div>

        <div v-if="timer.timerType=='openCard'">
            <button data-multiple="1" @click="opencard(1)" data-selector="openCard">1倍-</button>
            <button data-multiple="2" @click="opencard(2)" data-selector="openCard">2倍-</button>
            <button data-multiple="4" @click="opencard(4)" data-selector="openCard">4倍-</button>
        </div>

        <button v-if="timer.timerType=='readying'" @click="readying" data-selector="readying">准备</button>
        <button v-if="timer.timerType=='showdown'" @click="setshowdown" data-selector="showdown">摊牌</button>
    </div>

</template>

<script>
    export default {
        name: "tools-btn",
        props:{
            timer:{
                type:Object
            }
        },
        methods:{
            readying:function(){
                this.$emit("readying");
            },
            setwager:function(mult){
                this.$emit("setwager",mult);
            },
            opencard:function(mult){
                this.$emit("opencard",mult);
            },
            setshowdown:function(){
                this.$emit("setshowdown");
            }
        }
    }
</script>

<style scoped>
    .btns{
        text-align: center;
        height: 40px;
        clear: both;
    }
    .btns button{
        background: #ff9016;
        color: #fff;
        font-size: 16px;
        border-radius: 5px;
        padding: 5px 10px;
        margin-bottom: 10px;
    }
    .btns div{
        display: inline-block;
    }
</style>