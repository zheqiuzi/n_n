<template>
    <div class="timer">
        <p class="time">
            {{timer.remaining}}
        </p>
        <p>
            {{timer.timerType}}
        </p>
    </div>
</template>

<script>
    export default {
        props:{
            timer:{
                type:Object
            }
        }
    }
</script>

<style scoped>
    .timer{
        position: fixed;
        left: 45%;
        top: 45%;
        font-size: 14px;
        text-align: center;
    }
    .timer .time{
        width: 50px;
        height: 50px;
        background: #fff;
        display: block;
        text-align: center;
        line-height: 50px;
        border-radius: 50%;
        font-size: 22px;
        margin: auto;
    }
</style>