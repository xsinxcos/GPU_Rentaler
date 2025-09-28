<template>
    <div>
        <div class="container">
            <el-row>
                <el-col :lg="6" :xl="6" style="border-right: 1px solid #dcdfe6;">
                    <div style="padding-right: 10px">
                        <div style="margin-bottom: 24px; font-weight: 700">组织架构</div>
                        <el-divider></el-divider>
                        <el-tree
                            ref="treeRef"
                            :current-node-key="selectedNode?.id"
                            :data="treeData"
                            :default-expanded-keys="defaultExpandedKeys"
                            :expand-on-click-node="false"
                            :highlight-current="true"
                            node-key="id"
                            @node-click="handleNodeSelected"
                            @node-expand="handleNodeExpand"
                        >
                            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <el-icon v-if="data.type === OrgTypeEum.DEPART"><office-building/></el-icon>
                <el-icon v-else><SuitcaseLine/></el-icon>
                <span style="margin: 0 4px">{{ data.name }}</span>
                <el-dropdown trigger="hover">
                  <el-icon @click.stop><setting/></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <span v-action:organization:create>
                        <el-dropdown-item
                            @click.stop="handleAddOrgNode(data)">新增</el-dropdown-item>
                      </span>
                      <span v-if="data.id!==1" v-action:organization:create>
                        <el-dropdown-item
                            @click.stop="handleUpdateOrgNode(data,node?.parent?.data)"
                        >
                        编辑
                      </el-dropdown-item>
                      </span>
                      <span v-if="data.id!=1" v-action:organization:delete>
                        <el-dropdown-item @click.stop="saveDeleteOrgNode(data)">
                        <span style="color: #f56c6c">删除</span>
                      </el-dropdown-item>
                      </span>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </span>
                            </template>
                        </el-tree>
                    </div>
                </el-col>
                <el-col :lg="18" :xl="18" style="border-right: 1px solid #dcdfe6">
                    <div style="padding-left: 10px">
                        <div class="handle-box">
                            <el-input v-model="query.username" class="handle-input mr10" clearable
                                      placeholder="用户名"></el-input>
                            <el-select v-model="query.state" class="handle-select mr10" clearable placeholder="状态">
                                <el-option key="1" label="正常" value="NORMAL"></el-option>
                                <el-option key="2" label="锁定" value="LOCKED"></el-option>
                            </el-select>
                            <el-button :icon="Search" type="primary" @click="handleSearch">搜索</el-button>
                            <el-button v-action:user:create :icon="Plus"
                                       type="primary"
                                       @click="addVisible = true;Object.assign(form, new User())">新增
                            </el-button>
                        </div>
                        <el-table ref="multipleTable" :data="tableData" border class="table"
                                  header-cell-class-name="table-header">
                            <el-table-column align="center" label="ID" prop="id" width="55"></el-table-column>
                            <el-table-column label="用户名" prop="username"></el-table-column>
                            <el-table-column label="性别" prop="gender">
                                <template #default="{ row }">
                                    <span>{{ row.gender === 'MALE' ? '男' : '女' }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column align="center" label="头像">
                                <template #default="scope">
                                    <el-image
                                        :preview-src-list="[scope.row.avatar]"
                                        :src="scope.row.avatar"
                                        :z-index="10"
                                        class="table-td-thumb"
                                        preview-teleported
                                    >
                                    </el-image>
                                </template>
                            </el-table-column>
                            <el-table-column align="center" label="状态">
                                <template #default="scope">
                                    <el-tag
                                        :type="scope.row.state === 'NORMAL' ? 'success' : scope.row.state === 'LOCKED' ? 'danger' : ''"
                                    >
                                        {{
                                            scope.row.state === 'NORMAL' ? '正常' : scope.row.state === 'LOCKED' ? '锁定' : ''
                                        }}
                                    </el-tag>
                                </template>
                            </el-table-column>

                            <el-table-column label="注册时间" prop="createdTime"></el-table-column>
                            <el-table-column label="所属组织" prop="orgFullName"></el-table-column>
                            <el-table-column fixed="right" label="操作" width="300">
                                <template #default="scope">
                                    <el-popconfirm title="确定要禁用?" @confirm="handleDisable(scope.row)">
                                        <template #reference>
                                            <el-button v-if="scope.row.state==='NORMAL'" v-action:user:update :icon="Lock"
                                                       text>
                                                禁用
                                            </el-button>
                                        </template>
                                    </el-popconfirm>
                                    <el-popconfirm title="确定要启用?" @confirm="handleEnable(scope.row)">
                                        <template #reference>
                                            <el-button v-if="scope.row.state==='LOCKED'" v-action:user:update :icon="Unlock"
                                                       text>
                                                启用
                                            </el-button>
                                        </template>
                                    </el-popconfirm>
                                    <el-button v-action:user:update :icon="Edit" text
                                               @click="handleEdit(scope.$index, scope.row)">
                                        编辑
                                    </el-button>
                                    <el-button v-action:user:delete :icon="Delete" class="red" text
                                               @click="handleDelete(scope.row)">
                                        删除
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="pagination">
                            <el-pagination
                                :current-page="query.pageIndex"
                                :page-size="query.pageSize"
                                :total="pageTotal"
                                background
                                layout="total, prev, pager, next"
                                @current-change="handlePageChange"
                            ></el-pagination>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 新增组织架构树 -->
        <el-dialog v-model="addOrgDialogVisible" title="新增" width="30%">
            <el-form label-width="70px">
                <el-form-item label="上级节点">
                    <el-input v-model="orgForm.parentName" disabled></el-input>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="orgForm.name"></el-input>
                </el-form-item>
                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="orgForm.type">
                        <el-radio :label="OrgTypeEum.DEPART">部门</el-radio>
                        <el-radio :label="OrgTypeEum.JOB">岗位</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
				<span class="dialog-footer">
					<el-button @click="addOrgDialogVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveAddOrgNode">确 定</el-button>
				</span>
            </template>
        </el-dialog>

        <!-- 编辑组织架构树 -->
        <el-dialog v-model="updateOrgDialogVisible" title="编辑" width="30%">
            <el-form label-width="70px">
                <el-form-item label="上级节点">
                    <el-input v-model="orgForm.parentName" disabled></el-input>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="orgForm.name"></el-input>
                </el-form-item>
                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="orgForm.type" disabled>
                        <el-radio :label="OrgTypeEum.DEPART">部门</el-radio>
                        <el-radio :label="OrgTypeEum.JOB">岗位</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
				<span class="dialog-footer">
					<el-button @click="updateOrgDialogVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveUpdateOrgNode">确 定</el-button>
				</span>
            </template>
        </el-dialog>

        <!-- 新增用户弹出框 -->
        <el-dialog v-model="addVisible" title="新增" width="30%">
            <el-form label-width="70px">
                <el-form-item label="所属组织">
                    <el-input v-model="selectedNode.name" disabled></el-input>
                </el-form-item>
                <el-input v-show="false" v-model="form.avatar" hidden="hidden"></el-input>
                <el-form-item label="用户名">
                    <el-input v-model="form.username"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                        <el-radio label="MALE">男</el-radio>
                        <el-radio label="FEMALE">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="头像">
                    <AvatarUpload :img-src="form.avatar" @on-select="onAvatarSelect"/>
                </el-form-item>
            </el-form>
            <template #footer>
				<span class="dialog-footer">
					<el-button @click="addVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveAdd">确 定</el-button>
				</span>
            </template>
        </el-dialog>
        <!-- 编辑用户弹出框 -->
        <el-dialog v-model="editVisible" title="编辑" width="30%">
            <el-form label-width="70px">
                <el-form-item label="所属组织">
                    <el-input v-model="selectedNode.name" disabled></el-input>
                </el-form-item>
                <el-input v-show="false" v-model="form.avatar" hidden="hidden"></el-input>
                <el-form-item label="用户名">
                    <el-input v-model="form.username" disabled></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                        <el-radio label="MALE">男</el-radio>
                        <el-radio label="FEMALE">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="头像">
                    <AvatarUpload :img-src="form.avatar" @on-select="onAvatarSelect"/>
                </el-form-item>
            </el-form>
            <template #footer>
				<span class="dialog-footer">
					<el-button @click="editVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveEdit">确 定</el-button>
				</span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Delete, Edit, Lock, Plus, Search, Unlock} from '@element-plus/icons-vue';
import {createUser, deleteUser, disableUser, enableUser, updateUser} from "../api/user";
import {
    createOrganization,
    deleteOrganization,
    getOrganizationTree,
    getOrganizationUserList,
    updateOrganization
} from "../api/organization";
import cloneDeep from 'lodash/cloneDeep';
import AvatarUpload from "../components/AvatarUpload.vue";

interface OrgTreeNode {
    name: string
    id: number
    children?: OrgTreeNode[]
    type: OrgTypeEum
    isLeaf?: boolean
}

interface OrgSelectableTreeNode extends Partial<Omit<OrgTreeNode, 'children'>> {
    label: string
    value: number
    children?: OrgSelectableTreeNode[] | OrgTreeNode[]
}

enum OrgTypeEum {
    DEPART = 'DEPARTMENT',
    JOB = 'JOB',
}

const rootNode = {name: '根节点', id: 1, type: OrgTypeEum.DEPART, children: []};

const treeData = ref<OrgTreeNode[]>([rootNode]);
const defaultExpandedKeys = ref<number[]>([1]);
const selectedNode = ref<OrgTreeNode>(rootNode);

const handleNodeSelected = (node: OrgTreeNode) => {
    selectedNode.value = node;
    getUserData();
}

const handleNodeExpand = (d: any) => {
    // console.log(d, 'expand')
}
const reqAllNodes = async () => {
    const res = await getOrganizationTree({
        parentId: 1
    })
    treeData.value[0].children = res.data;

    const cloneTreeData = cloneDeep(treeData.value);
    convertOrgTree(cloneTreeData);
}
reqAllNodes();


class Org {
    id = 0;
    name = '';
    type = OrgTypeEum.DEPART;
    parentId = 1;
    parentName = '';
}

let orgForm = reactive(new Org());

//表格新增时
const addOrgDialogVisible = ref(false);

const updateOrgDialogVisible = ref(false);

const handleAddOrgNode = (data: OrgTreeNode) => {
    Object.assign(orgForm, new Org());
    orgForm.parentName = data.name;
    orgForm.parentId = data.id;
    addOrgDialogVisible.value = true;
}

const handleUpdateOrgNode = (data: OrgTreeNode, parent: OrgTreeNode) => {
    orgForm.id = data.id;
    orgForm.name = data.name;
    orgForm.type = data.type;
    orgForm.parentId = parent.id;
    orgForm.parentName = parent.name;
    updateOrgDialogVisible.value = true;
}

const saveAddOrgNode = async () => {
    await createOrganization(orgForm);
    addOrgDialogVisible.value = false;
    await reqAllNodes();
}

/**
 * @desc 转换数组对象的键值对 el-tree => el-tree-select
 */
const convertOrgTree = (orgTreeNode: Partial<(OrgTreeNode & { value: number, label: string })>[]) => {
    if (!orgTreeNode.length) {
        return;
    }

    for (let org of orgTreeNode) {
        org.value = org.id as number;
        org.label = org.name as string;

        if (org.children?.length) {
            convertOrgTree(org.children);
        }
    }
}

const saveUpdateOrgNode = async () => {
    await updateOrganization(orgForm.id, orgForm);
    updateOrgDialogVisible.value = false;
    await reqAllNodes();
}

const saveDeleteOrgNode = async (data: OrgTreeNode) => {
    // 二次确认删除
    ElMessageBox.confirm('确定要删除吗？', '提示', {
        type: 'warning'
    }).then(() => {
        deleteOrganization(data.id).then(res => {
            ElMessage.success('删除成功');
            reqAllNodes();
        });
    }).catch(() => {
    });

}

interface TableItem {
    id: number;
    username: string;
    gender: string;
    avatar: string;
    state: string;
    createdTime: string;
    orgFullName: string;
}

const query = reactive({
    username: '',
    state: '',
    pageIndex: 1,
    pageSize: 10
});
const tableData = ref<TableItem[]>([]);
const pageTotal = ref(0);
// 获取表格数据
const getUserData = () => {
    getOrganizationUserList(selectedNode.value.id, {
        page: query.pageIndex,
        size: query.pageSize,
        username: query.username || undefined,
        state: query.state || undefined,
    }).then(res => {
        tableData.value = res.data.list;
        pageTotal.value = res.data.total;
    });
};
getUserData();

// 查询操作
const handleSearch = () => {
    query.pageIndex = 1;
    getUserData();
};
// 分页导航
const handlePageChange = (val: number) => {
    query.pageIndex = val;
    getUserData();
};

// 删除操作
const handleDelete = (row: any) => {
    // 二次确认删除
    ElMessageBox.confirm('确定要删除吗？', '提示', {
        type: 'warning'
    }).then(() => {
        deleteUser(row.id).then(res => {
            getUserData();
        });
        ElMessage.success('删除成功');
    })
        .catch(() => {
        });
};

//表格新增时
const addVisible = ref(false);

// 表格编辑时弹窗和保存
const editVisible = ref(false);

class User {
    username = '';
    gender = 'MALE';
    avatar = '';
    organizationId = 1;
}

let id: number = 0;
let form = reactive(new User());

const saveAdd = () => {
    form.organizationId = selectedNode.value.id;
    createUser(form).then(res => {
        getUserData();
        ElMessage.success(`新增成功`);
        addVisible.value = false;
    });
};
const handleEdit = (index: number, row: any) => {
    id = row.id;
    form.username = row.username;
    form.gender = row.gender;
    form.avatar = row.avatar;
    editVisible.value = true;
};
const saveEdit = () => {
    editVisible.value = false;
    form.organizationId = selectedNode.value.id;
    console.log(form);
    updateUser(id, form).then(res => {
        getUserData();
        ElMessage.success(`修改成功`);
    });
};
const handleDisable = (row: any) => {
    disableUser(row.id).then(res => {
        getUserData();
        ElMessage.success(`禁用成功`);
    });
}
const handleEnable = (row: any) => {
    enableUser(row.id).then(res => {
        getUserData();
        ElMessage.success(`启用成功`);
    });
}
const onAvatarSelect = (imgUrl: string) => {
    form.avatar = imgUrl;
}

</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
}

.table {
    width: 100%;
    font-size: 14px;
}

.red {
    color: #F56C6C;
}

.mr10 {
    margin-right: 10px;
}

.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
</style>
