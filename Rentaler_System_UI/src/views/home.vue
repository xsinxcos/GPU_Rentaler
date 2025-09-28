<template>
    <v-header/>
    <v-sidebar/>
    <div :class="{ 'content-collapse': sidebar.collapse }" class="content-box">
        <v-tags></v-tags>
        <div class="content">
            <router-view v-slot="{ Component }">
                <transition mode="out-in" name="move">
                    <keep-alive :include="tags.nameList">
                        <component :is="Component"></component>
                    </keep-alive>
                </transition>
            </router-view>
        </div>
    </div>
</template>
<script lang="ts" setup>
import {useSidebarStore} from '../store/sidebar';
import {useTagsStore} from '../store/tags';

const sidebar = useSidebarStore();
const tags = useTagsStore();
</script>
